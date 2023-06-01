package com.example.picstorm.domain.model

import android.graphics.Bitmap

data class UserSearched(
    val id: Long,
    val nickname: String,
    val avatar: Bitmap?,
    val subscribed: Boolean?
)
