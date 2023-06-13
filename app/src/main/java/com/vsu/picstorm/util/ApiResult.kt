package com.vsu.picstorm.util

import com.vsu.picstorm.domain.model.enums.HttpStatus

enum class ApiStatus {
    SUCCESS,
    ERROR,
    LOADING
}

sealed class ApiResult <out T> (val status: ApiStatus, val statusCode: Int, val data: T?, val message:String?) {

    data class Success<out R>(val _data: R?): ApiResult<R>(
        status = ApiStatus.SUCCESS,
        statusCode = HttpStatus.OK.code,
        data = _data,
        message = null
    )

    data class Error(val code: Int, val exception: String): ApiResult<Nothing>(
        status = ApiStatus.ERROR,
        data = null,
        statusCode = code,
        message = exception
    )

    data class Loading<out R>(val isLoading: Boolean): ApiResult<R>(
        status = ApiStatus.LOADING,
        statusCode = 0,
        data = null,
        message = null
    )
}