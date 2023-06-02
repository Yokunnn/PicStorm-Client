package com.vsu.picstorm.util

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object RequestUtils {
    fun <T, R> requestFlow(
        callFunc: suspend () -> Call<R>,
        mapper: (R?) -> T?
    ): Flow<ApiResult<T>> {
        return callbackFlow {
            trySend(ApiResult.Loading(true))
            callFunc().enqueue(
                object : Callback<R> {
                    override fun onResponse(call: Call<R>, response: Response<R>) {
                        if (response.isSuccessful) {
                            val body = response.body()
                            val result = ApiResult.Success(mapper(body))
                            trySend(result)
                        } else {
                            val errorMsg = response.errorBody()?.toString() ?: "Something went wrong"
                            response.errorBody()?.close()
                            trySend(ApiResult.Error(errorMsg))
                        }
                    }

                    override fun onFailure(call: Call<R>, t: Throwable) {
                        val errorMsg = "No connection with server"
                        trySend(ApiResult.Error(errorMsg))
                    }
                }
            )
            awaitClose {}
        }
    }
}