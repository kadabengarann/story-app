package com.kadabengaran.storyapp.service.remote

import com.kadabengaran.storyapp.service.model.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("/v1/register")
    suspend fun register(
        @Body registerRequest: RegisterBody
    ): ResponseRegister

    @POST("/v1/login")
    suspend fun login(
        @Body loginRequest: LoginBody
    ): ResponseStory
}