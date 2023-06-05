package com.vsu.picstorm.data.mapper

import com.vsu.picstorm.data.model.response.TokenResponse
import com.vsu.picstorm.domain.model.Token

fun TokenResponse.mapToDomain(): Token {
    return Token(
        this.accessToken
    )
}