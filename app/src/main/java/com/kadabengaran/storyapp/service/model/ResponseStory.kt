package com.kadabengaran.storyapp.service.model

import com.google.gson.annotations.SerializedName

class ResponseStory (
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("loginResult")
    val loginResult: ResponseLogin
)

data class ResponseRegister(
    @field:SerializedName("message")
    val message: String,
)
data class ResponseLogin(
    @field:SerializedName("userId")
    val userId: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("token")
    val token: String,
)