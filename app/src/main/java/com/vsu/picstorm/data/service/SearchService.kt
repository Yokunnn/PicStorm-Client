package com.vsu.picstorm.data.service

import com.vsu.picstorm.data.model.response.UserLinesResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SearchService {

    @GET("user/search")
    fun search(
        @Header("Authorization") authHeader: String?,
        @Query("nickname") name: String,
        @Query("index") index: Int,
        @Query("size") size: Int
    ): Call<UserLinesResponse>

}