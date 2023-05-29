package com.example.picstorm.data.mapper

import com.example.picstorm.data.model.response.TokenResponse
import com.example.picstorm.domain.model.Token

fun TokenResponse.mapToDomain(): Token {
    return Token(
        this.accessToken,
        this.refreshToken
    )
}