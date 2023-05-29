package com.example.picstorm.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

object RequestUtils {
    fun <T> requestFlow(responseFunc: suspend () -> T): Flow<Request<T>> {
        return flow<Request<T>> {
            emit(Request.Success(responseFunc()))
        }.onStart {
            emit(Request.Loading())
        }.catch { error ->
            emit(Request.Error(error.message.toString()))
        }
    }
}