package com.kratosgado.chatapp.utils

import org.springframework.http.HttpStatus

class ApiException(
    val status: HttpStatus,
    message: String,
) : RuntimeException(message) {
    companion object {
        fun badRequest(message: String) = ApiException(HttpStatus.BAD_REQUEST, message)

        fun unauthorized(message: String) = ApiException(HttpStatus.UNAUTHORIZED, message)

        fun forbidden(message: String) = ApiException(HttpStatus.FORBIDDEN, message)

        fun notFound(message: String) = ApiException(HttpStatus.NOT_FOUND, message)

        fun conflict(message: String) = ApiException(HttpStatus.CONFLICT, message)

        fun unprocessableEntity(message: String) = ApiException(HttpStatus.UNPROCESSABLE_ENTITY, message)

        fun internalServerError(message: String) = ApiException(HttpStatus.INTERNAL_SERVER_ERROR, message)

        fun serviceUnavailable(message: String) = ApiException(HttpStatus.SERVICE_UNAVAILABLE, message)
    }
}
