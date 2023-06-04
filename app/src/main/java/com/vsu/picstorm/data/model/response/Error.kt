package com.vsu.picstorm.data.model.response

import com.google.gson.annotations.SerializedName

data class Error(
    @SerializedName("message")
    val message: String
)
