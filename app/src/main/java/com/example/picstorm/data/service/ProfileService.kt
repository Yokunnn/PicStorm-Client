package com.example.picstorm.data.service

import com.example.picstorm.data.model.response.ChangeRoleResponse
import com.example.picstorm.data.model.response.ProfileResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProfileService {

    @GET("user/{userId}/profile")
    suspend fun getProfile(
        @Header("Authorization") authHeader: String?,
        @Path("userId") userId: Long
    ): Response<ProfileResponse>

    @POST("user/avatar")
    suspend fun uploadAvatar(
        @Header("Authorization") authHeader: String?,
        @Body picture: RequestBody
    ): Response<Void>

    @PUT("user/{userId}/ban")
    suspend fun banUser(
        @Header("Authorization") authHeader: String?,
        @Path("userId") userId: Long
    ): Response<ChangeRoleResponse>

    @PUT("user/{userId}/admin")
    suspend fun changeAdmin(
        @Header("Authorization") authHeader: String?,
        @Path("userId") userId: Long
    ): Response<ChangeRoleResponse>
}