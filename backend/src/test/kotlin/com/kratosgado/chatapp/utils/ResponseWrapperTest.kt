package com.kratosgado.chatapp.utils

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@WebMvcTest(controllers = [ResponseWrapperTest.TestController::class])
@Import(ResponseWrapper::class)
class ResponseWrapperTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `should wrap String response in ApiResponse and return JSON`() {
        val expectedResponse = ApiResponse.ok("Hello World")
        val expectedJson = objectMapper.writeValueAsString(expectedResponse)

        mockMvc.perform(get("/test/string"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(expectedJson))
    }
    
    @Test
    fun `should wrap Object response in ApiResponse`() {
        val data = mapOf("key" to "value")
        val expectedResponse = ApiResponse.okWithData("success", data)
        val expectedJson = objectMapper.writeValueAsString(expectedResponse)
        
        mockMvc.perform(get("/test/object"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(expectedJson))
    }

    @Configuration
    class TestConfig {
        @Bean
        fun testController() = TestController()
    }

    @RestController
    class TestController {
        @GetMapping("/test/string")
        fun returnString(): String {
            return "Hello World"
        }
        
        @GetMapping("/test/object")
        fun returnObject(): Map<String, String> {
            return mapOf("key" to "value")
        }
    }
}
