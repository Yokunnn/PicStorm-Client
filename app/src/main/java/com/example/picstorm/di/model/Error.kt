package com.example.picstorm.di.model

import com.google.gson.annotations.SerializedName

data class Error(
    @SerializedName("message")
    val message: String
)
