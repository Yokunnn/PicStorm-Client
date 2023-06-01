package com.example.picstorm.data.model.request

data class SearchRequest(
    val name: String,
    val index: Int,
    val size: Int
)