package com.example.picstorm.data.repository

import com.example.picstorm.data.mapper.mapToDomain
import com.example.picstorm.data.model.request.RegisterRequest
import com.example.picstorm.data.service.RegisterService
import com.example.picstorm.domain.RegisterRepository
import com.example.picstorm.domain.model.Token
import com.example.picstorm.util.ApiResult
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
    ): Flow<ApiResult<Token>> {
        return requestFlow(
            { registerService.register(RegisterRequest(login, password, email)) },
            { value -> value?.mapToDomain() }
        )
    }

}