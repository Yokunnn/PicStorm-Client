package com.example.picstorm.data.service

import com.example.picstorm.data.model.response.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchService {

    @GET("user/search")
    suspend fun search(
        @Query("nickname") name: String,
        @Query("index") index: Int,
        @Query("size") size: Int
    ): SearchResponse

}