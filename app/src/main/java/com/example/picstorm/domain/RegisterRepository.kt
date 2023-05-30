package com.example.picstorm.domain

import com.example.picstorm.domain.model.Token
import com.example.picstorm.util.Request
import kotlinx.coroutines.flow.Flow

interface RegisterRepository {
    suspend fun register(login: String, password: String, email: String): Flow<Request<Token>>
}