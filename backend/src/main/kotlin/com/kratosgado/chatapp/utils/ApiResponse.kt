package com.kratosgado.chatapp.utils

import org.springframework.http.HttpStatus

data class ApiResponse(
    val status: HttpStatus,
    val message: String,
    val data: Any? = null,
) {
    companion object {
        fun ok(message: String) = ApiResponse(HttpStatus.OK, message)

        fun okWithData(
            message: String,
            data: Any?,
        ) = ApiResponse(HttpStatus.OK, message, data)

        fun created(message: String) = ApiResponse(HttpStatus.CREATED, message)

        fun createdWithData(
            message: String,
            data: Any?,
        ) = ApiResponse(HttpStatus.CREATED, message, data)
    }
}
