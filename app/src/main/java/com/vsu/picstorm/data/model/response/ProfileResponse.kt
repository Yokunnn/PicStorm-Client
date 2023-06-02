package com.vsu.picstorm.data.model.response

import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    @SerializedName("id")
    val userId: Long,
    @SerializedName("avatar")
    val avatar: String?,
    @SerializedName("nickname")
    val name: String,
    @SerializedName("role")
    val role: String,
    @SerializedName("subscribed")
    val subscribed: Boolean?,
    @SerializedName("subscriptionsCount")
    val subscriptions: Int,
    @SerializedName("subscribersCount")
    val subscribers: Int
)
