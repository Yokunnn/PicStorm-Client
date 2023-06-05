package com.vsu.picstorm.data.model.response

import com.google.gson.annotations.SerializedName

data class PictureResponse(
    @SerializedName("picture")
    val picture: String?,
)