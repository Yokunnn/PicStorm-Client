package com.vsu.picstorm.data.repository

import com.vsu.picstorm.data.mapper.mapToDomain
import com.vsu.picstorm.data.model.request.LoginRequest
import com.vsu.picstorm.data.service.LoginService
import com.vsu.picstorm.domain.LoginRepository
import com.vsu.picstorm.domain.model.Token
import com.vsu.picstorm.util.ApiResult
import com.vsu.picstorm.util.RequestUtils.requestFlow
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