package com.vsu.picstorm.data.service

import com.vsu.picstorm.data.model.response.UserLinesResponse
import com.vsu.picstorm.data.model.response.SubscribeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface SubscriptionService {

    @PUT("subscription/{userId}")
    suspend fun changeSubscribe(
        @Header("Authorization") authHeader: String?,
        @Path("userId") userId: Long
    ): Response<SubscribeResponse>

    @GET("subscriptions/{userId}")
    suspend fun getSubscriptions(
        @Header("Authorization") authHeader: String?,
        @Path("userId") userId: Long,
        @Query("index") index: Int,
        @Query("size") size: Int
    ): Response<UserLinesResponse>

    @GET("subscribers/{userId}")
    suspend fun getSubscribers(
        @Header("Authorization") authHeader: String?,
        @Path("userId") userId: Long,
        @Query("index") index: Int,
        @Query("size") size: Int
    ): Response<UserLinesResponse>

}