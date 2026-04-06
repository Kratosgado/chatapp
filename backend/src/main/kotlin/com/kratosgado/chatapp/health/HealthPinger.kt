package com.kratosgado.chatapp.health

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class HealthPinger {
    private val logger = LoggerFactory.getLogger(HealthPinger::class.java)
    private val restTemplate = RestTemplate()

    // Allows you to set the public Render URL via environment variable: APP_PING_URL
    @Value("\${app.ping.url:http://localhost:8080/health}")
    lateinit var pingUrl: String

    @Scheduled(fixedRate = 600000) // 600,000 ms = 10 minutes
    fun pingHealthEndpoint() {
        try {
            logger.info("Attempting to ping health endpoint at: {}", pingUrl)
            val response = restTemplate.getForObject(pingUrl, String::class.java)
            logger.info("Successfully pinged health endpoint. Response: {}", response)
        } catch (e: Exception) {
            logger.error("Failed to ping health endpoint: {}", e.message)
        }
    }
}
