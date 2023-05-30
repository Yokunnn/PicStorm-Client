package com.example.picstorm.data.model.request

data class RegisterRequest(
    val nickname: String,
    val password: String,
    val email: String
)