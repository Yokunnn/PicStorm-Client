package com.vsu.picstorm.data.mapper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.vsu.picstorm.data.model.response.UserLinesResponse
import com.vsu.picstorm.domain.model.UserLine

fun UserLinesResponse.mapToDomain(): List<UserLine> {
    var values: List<UserLine> = emptyList()
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
        val searched = UserLine(
            u.id,
            u.name,
            bitmap,
            sub
        )
        values = values + searched
    }
    return values
}