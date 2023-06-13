package com.vsu.picstorm.util

import com.google.gson.Gson
import com.vsu.picstorm.data.model.response.Error
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
                            val errorMsg: String = response.errorBody()?.let {
                                val error =
                                    Gson().fromJson(
                                        response.errorBody()!!.charStream(),
                                        Error::class.java
                                    )
                                response.errorBody()?.close()
                                error.message.trim()
                            } ?: "Что-то пошло не так"
                            trySend(ApiResult.Error(response.code(), errorMsg))
                        }
                    }

                    override fun onFailure(call: Call<R>, t: Throwable) {
                        val errorMsg = "Нет соединения с сервером"
                        trySend(ApiResult.Error(0, errorMsg))
                    }
                }
            )
            awaitClose {}
        }
    }
}