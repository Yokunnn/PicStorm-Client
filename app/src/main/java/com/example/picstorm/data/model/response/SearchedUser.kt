package com.example.picstorm.data.model.response

import com.google.gson.annotations.SerializedName

data class SearchedUser(
    @SerializedName("userId")
    val id: Int,
    @SerializedName("avatar")
    val avatar: Avatar,
    @SerializedName("nickname")
    val name: String,
    @SerializedName("subscribed")
    val subscribed: Boolean?
)
