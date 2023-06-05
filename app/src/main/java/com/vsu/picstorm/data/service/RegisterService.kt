package com.vsu.picstorm.data.service

import com.vsu.picstorm.data.model.request.RegisterRequest
import com.vsu.picstorm.data.model.response.TokenResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RegisterService {

    @POST("auth/register")
    fun register(@Body registerRequest: RegisterRequest): Call<TokenResponse>

}