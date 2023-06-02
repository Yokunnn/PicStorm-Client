package com.vsu.picstorm.data.mapper

import com.vsu.picstorm.data.model.response.UserLinesResponse
import com.vsu.picstorm.domain.model.UserLine

fun UserLinesResponse.mapToDomain(): List<UserLine> {
    var values: List<UserLine> = emptyList()
    for (u in this.values) {
        var sub: Boolean? = null
        u.subscribed?.let {
            sub = u.subscribed
        }
        val searched = UserLine(
            u.id,
            u.name,
            sub
        )
        values = values + searched
    }
    return values
}