package com.kadabengaran.storyapp.service.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.*
import com.kadabengaran.storyapp.service.Result
import com.kadabengaran.storyapp.service.database.StoryDatabase
import com.kadabengaran.storyapp.service.database.StoryEntity
import com.kadabengaran.storyapp.service.model.*
import com.kadabengaran.storyapp.service.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService,
) {
    suspend fun register(register: RegisterBody): Flow<Result<String>?> {
        return flow {
            emit(Result.Loading)
            try {
                val result = apiService.register(register).message
                emit(Result.Success(result))
            } catch (e: Exception) {
                Log.d("StoryRepository", "register: ${e.message.toString()} ")
                emit(Result.Error(e.message.toString()))
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun refresh() {
        storyDatabase.storyDao().deleteAll()
        storyDatabase.remoteKeysDao().deleteRemoteKeys()

    }

    fun login(login: LoginBody): Flow<Result<LoginResult>?> {
        return flow {
            emit(Result.Loading)
            try {
                val result = apiService.login(login).loginResult
                emit(Result.Success(result))
            } catch (e: Exception) {
                Log.d("StoryRepository", "register: ${e.message.toString()} ")
                emit(Result.Error(e.message.toString()))
            }
        }.flowOn(Dispatchers.IO)
    }

    fun getStories(): LiveData<PagingData<StoryEntity>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStories()
            }
        ).liveData
    }

    fun fetchStoryLocation(): Flow<Result<List<StoryItem>>> =
        flow {
            emit(Result.Loading)
            try {
                val result = apiService.getStories(1, 30, 1).listStory
                emit(Result.Success(result))
            } catch (e: Exception) {
                Log.d("StoryRepository", "fetchStoryList: ${e.message.toString()} ")
                emit(Result.Error(e.message.toString()))
            }
        }.flowOn(Dispatchers.IO)

    suspend fun postStory(
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ): Flow<Result<FileUploadResponse>> {
        return flow {
            emit(Result.Loading)
            try {
                val result = apiService.uploadImage(file, description, lat, lon)
                emit(Result.Success(result))
            } catch (e: Exception) {
                Log.d("StoryRepository", "UploadStory: ${e.message.toString()} ")
                emit(Result.Error(e.message.toString()))
            }
        }.flowOn(Dispatchers.IO)
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            storyDatabase: StoryDatabase,
            apiService: ApiService,
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(storyDatabase, apiService)
            }.also { instance = it }
    }
}