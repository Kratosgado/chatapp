package com.kratosgado.chatapp.utils

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.*
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

class ResponseWrapperTest {

    private val objectMapper = mockk<ObjectMapper>()
    private val responseWrapper = ResponseWrapper(objectMapper)

    private val methodParameter = mockk<MethodParameter>()
    private val serverRequest = mockk<ServerHttpRequest>()
    private val serverResponse = mockk<ServerHttpResponse>(relaxed = true)
    private val servletRequest = mockk<HttpServletRequest>()

    @BeforeEach
    fun setup() {
        // Setup the static RequestContextHolder for 'supports' method
        val attrs = ServletRequestAttributes(servletRequest)
        RequestContextHolder.setRequestAttributes(attrs)
    }

    @AfterEach
    fun tearDown() {
        RequestContextHolder.resetRequestAttributes()
    }

    @Test
    fun `supports should return false for ignored paths`() {
        every { servletRequest.requestURI } returns "/swagger-ui.html"

        val result = responseWrapper.supports(methodParameter, HttpMessageConverter::class.java)

        assertFalse(result)
    }

    @Test
    fun `supports should return false if return type is already ApiResponse`() {
        every { servletRequest.requestURI } returns "/api/test"
        every { methodParameter.parameterType } returns ApiResponse::class.java

        val result = responseWrapper.supports(methodParameter, HttpMessageConverter::class.java)

        assertFalse(result)
    }

    @Test
    fun `supports should return true for normal controller methods`() {
        every { servletRequest.requestURI } returns "/api/users"
        every { methodParameter.parameterType } returns Any::class.java

        val result = responseWrapper.supports(methodParameter, HttpMessageConverter::class.java)

        assertTrue(result)
    }
    //
    // @Test
    // fun `beforeBodyWrite should wrap String and return JSON string`() {
    //     val body = "Hello World"
    //     val expectedJson = "{\"message\":\"Hello World\"}"
    //
    //     every { objectMapper.writeValueAsString(any()) } returns expectedJson
    //
    //     val result = responseWrapper.beforeBodyWrite(
    //         body, methodParameter, MediaType.TEXT_PLAIN, mockk(), serverRequest, serverResponse
    //     )
    //
    //     assertEquals(expectedJson, result)
    //     // Verify content type was changed to JSON
    //     verify { serverResponse.headers.contentType = MediaType.APPLICATION_JSON }
    // }
    //
    // @Test
    // fun `beforeBodyWrite should wrap generic object in ApiResponse`() {
    //     val body = mapOf("id" to 1)
    //
    //     val result = responseWrapper.beforeBodyWrite(
    //         body, methodParameter, MediaType.APPLICATION_JSON, mockk(), serverRequest, serverResponse
    //     )
    //
    //     assertTrue(result is ApiResponse)
    //     // You might want to assert the specific fields of your ApiResponse here
    // }
}
