package com.kadabengaran.storyapp.service.model

data class User(
    val name: String,
    val token: String,
    val isLogin: Boolean
)

data class RegisterBody(
    var name: String,
    var email: String,
    var password: String
)

data class LoginBody(
    val email: String,
    val password: String
)