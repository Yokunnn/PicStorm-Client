package com.vsu.picstorm.data.model.response

import com.google.gson.annotations.SerializedName

data class ReactionResponse(
    @SerializedName("reaction")
    val reaction:String?
)
