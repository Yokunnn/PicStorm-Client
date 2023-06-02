package com.vsu.picstorm.data.mapper

import com.vsu.picstorm.data.model.response.ProfileResponse
import com.vsu.picstorm.domain.model.Profile
import com.vsu.picstorm.domain.model.enums.UserRole

fun ProfileResponse.mapToDomain(): Profile {
    var sub: Boolean? = null
    subscribed?.let {
        sub = subscribed
    }
    return Profile(
        userId, name, UserRole.valueOf(role), sub, subscriptions, subscribers
    )
}