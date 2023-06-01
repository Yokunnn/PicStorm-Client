package com.example.picstorm.data.service

import okhttp3.MultipartBody
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface PublicationService {

    @Multipart
    @POST("publication/")
    suspend fun loadPhoto(
        @Header("Authorization") authHeader: String?,
        @Part uploadPicture: MultipartBody.Part
    ): Void
}