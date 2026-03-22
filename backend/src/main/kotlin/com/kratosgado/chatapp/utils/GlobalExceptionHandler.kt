package com.kratosgado.chatapp.utils

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationErrors(ex: MethodArgumentNotValidException): ResponseEntity<*> {
        val errors: MutableMap<String, String?> = HashMap()
        ex.bindingResult.fieldErrors.forEach { e -> errors[e.field] = e.defaultMessage }
        return ResponseEntity.badRequest().body(errors)
    }

    @ExceptionHandler(ApiException::class)
    fun handleApiExceptions(ex: ApiException): ResponseEntity<*> =
        ResponseEntity.status(ex.status).body(ApiResponse(ex.status, ex.message ?: ""))
}
