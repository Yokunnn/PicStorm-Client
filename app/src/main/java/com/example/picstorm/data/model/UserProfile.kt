package com.example.picstorm.data.model

import android.graphics.Bitmap
import com.example.picstorm.domain.model.enums.UserRole
import com.google.gson.annotations.SerializedName

data class UserProfile(
    @SerializedName("id")
    val id: Long,
    @SerializedName("avatar")
    val avatar: Bitmap,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("role")
    val userRole: UserRole,
    @SerializedName("subscribed")
    val subscribed: Boolean,
    @SerializedName("subscriptionsCount")
    val subscriptionsCount: Long,
    @SerializedName("subscribersCount")
    val subscribersCount: Long
)
