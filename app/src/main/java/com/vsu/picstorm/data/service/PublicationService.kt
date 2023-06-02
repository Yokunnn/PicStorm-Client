package com.vsu.picstorm.data.service

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface PublicationService {

    @POST("publication/")
    suspend fun loadPhoto(
        @Header("Authorization") authHeader: String?,
        @Body picture: ByteArray
    ): Response<Void>
}