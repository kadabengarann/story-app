package com.kadabengaran.storyapp.service.remote

import com.kadabengaran.storyapp.service.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {
    @POST("register")
    suspend fun register(
        @Body registerRequest: RegisterBody
    ): ResponseRegister

    @POST("login")
    suspend fun login(
        @Body loginRequest: LoginBody
    ): ResponseLogin

    @GET("stories")
    suspend fun getStoryList(
        @Header("Authorization") token: String
    ): ResponseStory

    @Multipart
    @POST("stories")
    suspend fun uploadImage(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): FileUploadResponse
}