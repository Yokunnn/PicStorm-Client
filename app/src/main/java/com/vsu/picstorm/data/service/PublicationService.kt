package com.vsu.picstorm.data.service

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface PublicationService {

    @POST("publication/")
    fun loadPhoto(
        @Header("Authorization") authHeader: String?,
        @Body picture: RequestBody
    ): Call<Void>
}