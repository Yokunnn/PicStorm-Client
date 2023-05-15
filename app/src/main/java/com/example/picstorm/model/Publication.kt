package com.example.picstorm.model

import android.graphics.Bitmap
import java.time.Instant

data class Publication(
    val nickname: String,
    val avatar: Bitmap,
    val pic: Bitmap,
    val rating: Long,
    val reactionType: ReactionType,
    val date: Instant
)
