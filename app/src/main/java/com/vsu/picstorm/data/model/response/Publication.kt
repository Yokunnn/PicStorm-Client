package com.vsu.picstorm.data.model.response

import com.google.gson.annotations.SerializedName
import java.time.Instant

data class Publication(
    @SerializedName("publicationId")
    val id: Long,
    @SerializedName("ownerId")
    val ownerId: Long,
    @SerializedName("ownerNickname")
    val ownerName: String,
    @SerializedName("rating")
    val rating: Long,
    @SerializedName("uploaded")
    val uploadDate: String,
    @SerializedName("userReaction")
    val react: String?
)
