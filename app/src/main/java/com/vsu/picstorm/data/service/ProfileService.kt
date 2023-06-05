package com.vsu.picstorm.data.service

import com.vsu.picstorm.data.model.response.ChangeRoleResponse
import com.vsu.picstorm.data.model.response.PictureResponse
import com.vsu.picstorm.data.model.response.ProfileResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProfileService {

    @GET("user/{userId}/profile")
    fun getProfile(
        @Header("Authorization") authHeader: String?,
        @Path("userId") userId: Long
    ): Call<ProfileResponse>

    @POST("user/avatar")
    fun uploadAvatar(
        @Header("Authorization") authHeader: String?,
        @Body picture: RequestBody
    ): Call<Void>

    @GET("user/{userId}/avatar")
    fun getAvatar(
        @Path("userId") userId: Long
    ): Call<PictureResponse>

    @PUT("user/{userId}/ban")
    fun banUser(
        @Header("Authorization") authHeader: String?,
        @Path("userId") userId: Long
    ): Call<ChangeRoleResponse>

    @PUT("user/{userId}/admin")
    fun changeAdmin(
        @Header("Authorization") authHeader: String?,
        @Path("userId") userId: Long
    ): Call<ChangeRoleResponse>
}