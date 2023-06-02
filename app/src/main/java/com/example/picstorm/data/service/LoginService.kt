package com.example.picstorm.data.service

import com.example.picstorm.data.model.request.LoginRequest
import com.example.picstorm.data.model.response.TokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {

    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<TokenResponse>

}