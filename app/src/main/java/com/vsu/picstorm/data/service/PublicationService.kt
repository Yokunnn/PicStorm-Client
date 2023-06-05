package com.vsu.picstorm.data.service

import com.vsu.picstorm.data.model.response.FeedResponse
import com.vsu.picstorm.data.model.response.PictureResponse
import com.vsu.picstorm.data.model.response.ReactionResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface PublicationService {

    @POST("publication/")
    fun loadPhoto(
        @Header("Authorization") authHeader: String?,
        @Body picture: RequestBody
    ): Call<Void>

    @GET("publication/feed")
    fun getFeed(
        @Header("Authorization") authHeader: String?,
        @Query("dateFilter") dateFilter: String,
        @Query("sortFilter") sortFilter: String,
        @Query("userFilter") userFilter: String,
        @Query("filterUser") specifiedId: Long?,
        @Query("index") index: Int,
        @Query("size") size: Int
    ): Call<FeedResponse>

    @GET("publication/{publicationId}/picture")
    fun getPublicationPhoto(@Path("publicationId") publicationId: Long): Call<PictureResponse>

    @PUT("publication/{publicationId}")
    fun banPublication(
        @Header("Authorization") authHeader: String?,
        @Path("publicationId") publicationId: Long
    ): Call<Void>

    @DELETE("publication/{publicationId}")
    fun deletePublication(
        @Header("Authorization") authHeader: String?,
        @Path("publicationId") publicationId: Long
    ): Call<Void>

    @PUT("publication/{publicationId}/reaction")
    fun reactPublication(
        @Header("Authorization") authHeader: String?,
        @Path("publicationId") publicationId: Long,
        @Query("reaction") reaction: String?
    ): Call<ReactionResponse>
}