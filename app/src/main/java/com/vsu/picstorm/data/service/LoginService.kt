package com.vsu.picstorm.data.service

import com.vsu.picstorm.data.model.request.LoginRequest
import com.vsu.picstorm.data.model.response.TokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {

    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<TokenResponse>

}