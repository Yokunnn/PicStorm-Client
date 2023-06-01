package com.example.picstorm.data.mapper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.picstorm.data.model.response.SearchResponse
import com.example.picstorm.domain.model.UserSearched

fun SearchResponse.mapToDomain(): List<UserSearched> {
    var values: List<UserSearched> = emptyList()
    for (u in this.values) {
        var bitmap: Bitmap? = null
        u.avatar?.let {
            bitmap = BitmapFactory.decodeByteArray(
                u.avatar.data.toByteArray(),
                0,
                u.avatar.data.toByteArray().size
            )
        }
        var sub: Boolean? = null
        u.subscribed?.let {
            sub = u.subscribed
        }
        val searched = UserSearched(
            u.id,
            u.name,
            bitmap,
            sub
        )
        values = values + searched
    }
    return values
}