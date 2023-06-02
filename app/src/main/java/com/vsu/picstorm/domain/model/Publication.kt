package com.vsu.picstorm.domain.model

import android.graphics.Bitmap
import com.vsu.picstorm.domain.model.enums.ReactionType
import java.time.Instant

data class Publication(
    val nickname: String,
    val avatar: Bitmap,
    val pic: Bitmap,
    val rating: Long,
    val reactionType: ReactionType,
    val date: Instant
)
