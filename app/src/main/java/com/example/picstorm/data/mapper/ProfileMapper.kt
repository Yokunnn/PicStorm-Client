package com.example.picstorm.data.mapper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.example.picstorm.data.model.response.ProfileResponse
import com.example.picstorm.domain.model.Profile
import com.example.picstorm.domain.model.enums.UserRole

fun ProfileResponse.mapToDomain(): Profile {
    var bitmap: Bitmap? = null
    avatar?.let {
        val byteData: ByteArray = Base64.decode(avatar, Base64.DEFAULT)
        bitmap = BitmapFactory.decodeByteArray(
            byteData,
            0,
            byteData.size
        )
    }
    var sub: Boolean? = null
    subscribed?.let {
        sub = subscribed
    }
    return Profile(
        userId, bitmap, name, UserRole.valueOf(role), sub, subscriptions, subscribers
    )
}