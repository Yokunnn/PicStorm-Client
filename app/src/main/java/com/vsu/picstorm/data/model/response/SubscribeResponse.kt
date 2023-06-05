package com.vsu.picstorm.data.model.response

import com.google.gson.annotations.SerializedName

data class SubscribeResponse(
    @SerializedName("id")
    val id: Long?,
    @SerializedName("created")
    val date: String?
)
