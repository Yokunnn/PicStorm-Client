package com.vsu.picstorm.domain.model

import com.vsu.picstorm.domain.model.enums.UserRole

data class Profile(
    val userId: Long,
    val name: String,
    val role: UserRole,
    val subscribed: Boolean?,
    val subscriptions: Int,
    val subscribers: Int
)
