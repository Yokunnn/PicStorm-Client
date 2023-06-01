package com.example.picstorm.data.model.response

import com.google.gson.annotations.SerializedName

data class SearchResponse(
    @SerializedName("values")
    val values: List<SearchedUser>,
    @SerializedName("index")
    val index: Int,
    @SerializedName("size")
    val size: Int,
    @SerializedName("last")
    val last: Boolean
)