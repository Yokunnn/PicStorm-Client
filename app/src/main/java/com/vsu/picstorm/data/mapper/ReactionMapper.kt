package com.vsu.picstorm.data.mapper

import com.vsu.picstorm.data.model.response.ReactionResponse
import com.vsu.picstorm.domain.model.enums.ReactionType

fun ReactionResponse.mapToDomain(): ReactionType? {
    reaction?.let {
        if (reaction == "LIKE") {
            return ReactionType.LIKE
        } else return ReactionType.DISLIKE
    } ?: return null
}