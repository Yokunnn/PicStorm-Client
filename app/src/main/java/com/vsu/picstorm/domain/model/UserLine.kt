package com.vsu.picstorm.domain.model

data class UserLine(
    val id: Long,
    val nickname: String,
    var subscribed: Boolean?
)
