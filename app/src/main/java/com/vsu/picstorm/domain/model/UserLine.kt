package com.vsu.picstorm.domain.model

import android.graphics.Bitmap

data class UserLine(
    val id: Long,
    val nickname: String,
    val avatar: Bitmap?,
    val subscribed: Boolean?
)
