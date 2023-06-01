package com.example.picstorm.data.model.response

import com.google.gson.annotations.SerializedName

data class Avatar(
    @SerializedName("pictureType")
    val type: String,
    @SerializedName("data")
    val data: String
)
