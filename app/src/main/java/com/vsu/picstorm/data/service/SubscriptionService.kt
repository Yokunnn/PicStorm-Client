package com.vsu.picstorm.data.service

import com.vsu.picstorm.data.model.response.SubscribeResponse
import com.vsu.picstorm.data.model.response.UserLinesResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface SubscriptionService {

    @PUT("subscription/{userId}")
    fun changeSubscribe(
        @Header("Authorization") authHeader: String?,
        @Path("userId") userId: Long
    ): Call<SubscribeResponse>

    @GET("subscriptions/{userId}")
    fun getSubscriptions(
        @Header("Authorization") authHeader: String?,
        @Path("userId") userId: Long,
        @Query("index") index: Int,
        @Query("size") size: Int
    ): Call<UserLinesResponse>

    @GET("subscribers/{userId}")
    fun getSubscribers(
        @Header("Authorization") authHeader: String?,
        @Path("userId") userId: Long,
        @Query("index") index: Int,
        @Query("size") size: Int
    ): Call<UserLinesResponse>

}