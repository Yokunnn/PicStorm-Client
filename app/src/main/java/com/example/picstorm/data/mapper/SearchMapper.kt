package com.example.picstorm.data.mapper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.example.picstorm.data.model.response.SearchResponse
import com.example.picstorm.domain.model.UserSearched

fun SearchResponse.mapToDomain(): List<UserSearched> {
    var values: List<UserSearched> = emptyList()
    for (u in this.values) {
        var bitmap: Bitmap? = null
        u.avatar?.let {
            val byteData: ByteArray = Base64.decode(u.avatar, Base64.DEFAULT)
            bitmap = BitmapFactory.decodeByteArray(
                byteData,
                0,
                byteData.size
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