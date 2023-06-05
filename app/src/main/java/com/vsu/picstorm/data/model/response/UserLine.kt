package com.vsu.picstorm.data.model.response

import com.google.gson.annotations.SerializedName

data class UserLine(
    @SerializedName("userId")
    val id: Long,
    @SerializedName("nickname")
    val name: String,
    @SerializedName("subscribed")
    val subscribed: Boolean?
)
