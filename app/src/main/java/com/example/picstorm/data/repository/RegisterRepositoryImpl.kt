package com.example.picstorm.data.repository

import com.example.picstorm.data.mapper.mapToDomain
import com.example.picstorm.data.model.request.RegisterRequest
import com.example.picstorm.data.service.RegisterService
import com.example.picstorm.domain.RegisterRepository
import com.example.picstorm.domain.model.Token
import com.example.picstorm.util.Request
import com.example.picstorm.util.RequestUtils.requestFlow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RegisterRepositoryImpl @Inject constructor(
    private val registerService: RegisterService
) : RegisterRepository {

    override suspend fun register(
        login: String,
        password: String,
        email: String
    ): Flow<Request<Token>> {
        return requestFlow {
            val registerResponse = registerService.register(RegisterRequest(login, password, email))
            val token = registerResponse.mapToDomain()
            token
        }
    }

}