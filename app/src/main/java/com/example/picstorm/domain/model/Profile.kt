package com.example.picstorm.domain.model

import android.graphics.Bitmap

data class Profile(
    val userId: Long,
    val avatar: Bitmap?,
    val name: String,
    val role: String,
    val subscribed: Boolean?,
    val subscriptions: Int,
    val subscribers: Int
)
