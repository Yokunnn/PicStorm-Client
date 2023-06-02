package com.example.picstorm.data.service

import com.example.picstorm.data.model.request.RegisterRequest
import com.example.picstorm.data.model.response.TokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RegisterService {

    @POST("auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<TokenResponse>

}