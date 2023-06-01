package com.example.picstorm.util

fun createAuthHeader(token: String?): String? {
    var header: String? = null
    token?.let {
        header = "Bearer $token"
    }
    return header
}