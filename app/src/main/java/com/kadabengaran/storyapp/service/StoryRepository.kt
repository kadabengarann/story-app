package com.kadabengaran.storyapp.service

import android.util.Log
import com.kadabengaran.storyapp.service.model.LoginBody
import com.kadabengaran.storyapp.service.model.RegisterBody
import com.kadabengaran.storyapp.service.model.ResponseLogin
import com.kadabengaran.storyapp.service.model.ResponseRegister
import com.kadabengaran.storyapp.service.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class StoryRepository private constructor(
    private val apiService: ApiService,
) {
    fun register(register: RegisterBody): Flow<Result<String>?> {
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
    fun login(login: LoginBody): Flow<Result<ResponseLogin>?> {
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

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService)
            }.also { instance = it }
    }
}