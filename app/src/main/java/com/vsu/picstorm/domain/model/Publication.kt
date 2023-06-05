package com.vsu.picstorm.domain.model

import com.vsu.picstorm.domain.model.enums.ReactionType
import java.time.Instant

data class Publication(
    val id: Long,
    val ownerId: Long,
    val ownerNickname: String,
    var rating: Long,
    val date: Instant,
    var reactionType: ReactionType?,
    var publicationHeight: Int?
)
