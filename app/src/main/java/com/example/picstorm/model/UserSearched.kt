package com.example.picstorm.model

import android.graphics.Bitmap

data class UserSearched(
    val nickname: String,
    val avatar: Bitmap,
    val subscribed: Boolean
)
