package com.vsu.picstorm.data.model.response

import com.google.gson.annotations.SerializedName

data class SearchedUser(
    @SerializedName("userId")
    val id: Long,
    @SerializedName("avatar")
    val avatar: String?,
    @SerializedName("nickname")
    val name: String,
    @SerializedName("subscribed")
    val subscribed: Boolean?
)
