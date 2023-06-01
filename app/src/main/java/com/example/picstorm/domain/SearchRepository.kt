package com.example.picstorm.domain

import com.example.picstorm.domain.model.UserSearched
import com.example.picstorm.util.Request
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    suspend fun search(token: String?, name: String, index: Int, size: Int): Flow<Request<List<UserSearched>>>
}