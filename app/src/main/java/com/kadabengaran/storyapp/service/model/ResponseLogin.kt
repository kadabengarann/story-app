package com.kadabengaran.storyapp.service.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

class ResponseStory(
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("listStory")
    val listStory: List<StoryItem>
)

@Parcelize
data class StoryItem(
    @field:SerializedName("id")
    val id: String,
    @field:SerializedName("name")
    val name: String,
    @field:SerializedName("description")
    val description: String,
    @field:SerializedName("photoUrl")
    val photoUrl: String,
    @field:SerializedName("createdAt")
    val createdAt: String,
    ):Parcelable

class ResponseLogin(
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("loginResult")
    val loginResult: LoginResult
)

data class ResponseRegister(
    @field:SerializedName("message")
    val message: String,
)
data class LoginResult(
    @field:SerializedName("userId")
    val userId: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("token")
    val token: String,
)
data class FileUploadResponse(
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)
