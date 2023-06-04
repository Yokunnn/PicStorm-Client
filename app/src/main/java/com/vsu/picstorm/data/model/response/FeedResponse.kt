package com.vsu.picstorm.data.model.response

import com.google.gson.annotations.SerializedName

data class FeedResponse(
    @SerializedName("values")
    val values: List<Publication>,
    @SerializedName("index")
    val index: Int,
    @SerializedName("size")
    val size: Int,
    @SerializedName("last")
    val last: Boolean
)
