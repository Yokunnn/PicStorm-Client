package com.example.picstorm.data.repository

import com.example.picstorm.data.mapper.mapToDomain
import com.example.picstorm.data.model.request.LoginRequest
import com.example.picstorm.data.service.LoginService
import com.example.picstorm.domain.LoginRepository
import com.example.picstorm.domain.model.Token
import com.example.picstorm.util.Request
import com.example.picstorm.util.RequestUtils.requestFlow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val loginService: LoginService
) : LoginRepository {

    override suspend fun login(login: String, password: String): Flow<Request<Token>> {
        return requestFlow {
            val loginResponse = loginService.login(LoginRequest(login, password))
            val token = loginResponse.mapToDomain()
            token
        }
    }

}