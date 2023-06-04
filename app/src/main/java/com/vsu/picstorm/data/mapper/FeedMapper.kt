package com.vsu.picstorm.data.mapper

import com.vsu.picstorm.data.model.response.FeedResponse
import com.vsu.picstorm.domain.model.Publication
import com.vsu.picstorm.domain.model.enums.ReactionType
import java.time.Instant

fun FeedResponse.mapToDomain(): List<Publication> {
    var values: List<Publication> = emptyList()
    for (u in this.values) {
        var reactionType: ReactionType? = null
        u.react?.let {
            reactionType = if (u.react == "LIKE") {
                ReactionType.LIKE
            } else {
                ReactionType.DISLIKE
            }
        }
        val publication =
            Publication(u.id, u.ownerId, u.ownerName, u.rating, Instant.parse(u.uploadDate), reactionType)
        values = values + publication
    }
    return values
}