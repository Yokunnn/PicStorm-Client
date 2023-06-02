package com.example.picstorm.data.service

import com.example.picstorm.data.model.response.ProfileResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ProfileService {

    @GET("user/{userId}/profile")
    suspend fun getProfile(@Path("userId") userId: Long) : Response<ProfileResponse>

}