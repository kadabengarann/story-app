package com.kadabengaran.storyapp.service.data

import com.kadabengaran.storyapp.DummyDatas
import com.kadabengaran.storyapp.service.model.*
import com.kadabengaran.storyapp.service.remote.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody

class FakeApiService : ApiService {
    private val dummyResponse = DummyDatas.generateDummyStoryResponse()
    /*override suspend fun getNews(apiKey: String): NewsResponse {
        return dummyResponse
    }*/

    override suspend fun register(registerRequest: RegisterBody): ResponseRegister {
        TODO("Not yet implemented")
    }

    override suspend fun login(loginRequest: LoginBody): ResponseLogin {
        TODO("Not yet implemented")
    }

    override suspend fun getStoryList(token: String): ResponseStory {
        return dummyResponse
    }

    override suspend fun uploadImage(
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ): FileUploadResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getStories(page: Int, size: Int, location: Int): ResponseStory {
        TODO("Not yet implemented")
    }
}