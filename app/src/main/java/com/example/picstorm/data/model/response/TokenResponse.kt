package com.example.picstorm.data.model.response

import com.google.gson.annotations.SerializedName

data class TokenResponse(
    @SerializedName("accessToken")
    val accessToken: String
)
