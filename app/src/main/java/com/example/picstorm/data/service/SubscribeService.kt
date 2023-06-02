package com.example.picstorm.data.service

import com.example.picstorm.data.model.response.SubscribeResponse
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path

interface SubscribeService {

    @PUT("subscription/{userId}")
    suspend fun changeSubscribe(
        @Header("Authorization") authHeader: String?,
        @Path("userId") userId: Long
    ): Response<SubscribeResponse>

}