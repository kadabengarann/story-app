package com.kadabengaran.storyapp.service.model

data class User(
    val email: String,
    val token: String,
    val isLogin: Boolean
)