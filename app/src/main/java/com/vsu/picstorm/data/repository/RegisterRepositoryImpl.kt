package com.vsu.picstorm.data.repository

import com.vsu.picstorm.data.mapper.mapToDomain
import com.vsu.picstorm.data.model.request.RegisterRequest
import com.vsu.picstorm.data.service.RegisterService
import com.vsu.picstorm.domain.RegisterRepository
import com.vsu.picstorm.domain.model.Token
import com.vsu.picstorm.util.ApiResult
import com.vsu.picstorm.util.RequestUtils.requestFlow
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