package com.kratosgado.chatapp.utils

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice

@ControllerAdvice
class ResponseWrapper(private val objectMapper: ObjectMapper) : ResponseBodyAdvice<Any> {
    companion object {
        private val IGNORED_PATHS = listOf("/docs/**", "/swagger-ui.html", "/swagger-ui/**")
    }

    override fun supports(
        returnType: MethodParameter,
        converterType: Class<out HttpMessageConverter<*>>,
    ): Boolean {
        val attr = RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes
        if (attr != null) {
            val path = attr.request.requestURI
            if (IGNORED_PATHS.contains(path)) return false
        }

        return returnType.parameterType != ApiResponse::class.java &&
            returnType.parameterType != ResponseEntity::class.java
    }

    override fun beforeBodyWrite(
        body: Any?,
        returnType: MethodParameter,
        selectedContentType: MediaType,
        selectedConverterType: Class<out HttpMessageConverter<*>>,
        request: ServerHttpRequest,
        response: ServerHttpResponse,
    ): Any? {
        if (body is String) {
            response.headers.contentType = MediaType.APPLICATION_JSON
            return objectMapper.writeValueAsString(ApiResponse.ok(body))
        }

        if (body is ApiResponse) return body

        return ApiResponse.okWithData("success", body)
    }
}
