package com.example.picstorm.domain

import com.example.picstorm.domain.model.Token
import com.example.picstorm.util.Request
import kotlinx.coroutines.flow.Flow

interface LoginRepository {
    suspend fun login(Login: String, password: String): Flow<Request<Token>>
}