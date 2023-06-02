package com.example.picstorm.data.repository

import com.example.picstorm.data.mapper.mapToDomain
import com.example.picstorm.data.model.request.LoginRequest
import com.example.picstorm.data.service.LoginService
import com.example.picstorm.domain.LoginRepository
import com.example.picstorm.domain.model.Token
import com.example.picstorm.util.ApiResult
import com.example.picstorm.util.RequestUtils.requestFlow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val loginService: LoginService
) : LoginRepository {

    override suspend fun login(login: String, password: String): Flow<ApiResult<Token>> {
        return requestFlow(
            { loginService.login(LoginRequest(login, password)) },
            { value -> value?.mapToDomain() }
        )
    }

}