package com.vsu.picstorm.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

object RequestUtils {
    fun <T, R> requestFlow(responseFunc: suspend () -> Response<R>, mapper: (R?) -> T?): Flow<ApiResult<T>> {
        return flow {
            emit(ApiResult.Loading(true))
            val response = responseFunc()
            if (response.isSuccessful) {
                val body = response.body()
                val result = ApiResult.Success(mapper(body))
                emit(result)
            } else {
                val errorMsg = response.errorBody()?.toString() ?: "Something went wrong"
                response.errorBody()?.close()
                emit(ApiResult.Error(errorMsg))
            }
        }
    }
}