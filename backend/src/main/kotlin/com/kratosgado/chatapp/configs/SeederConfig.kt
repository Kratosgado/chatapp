package com.kratosgado.chatapp.configs

import com.kratosgado.chatapp.users.User
import com.kratosgado.chatapp.users.UserRepo
import net.datafaker.Faker
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class SeederConfig(
    private val userRepo: UserRepo,
    private val passwordEncoder: PasswordEncoder,
    private val env: Environment,
) : CommandLineRunner {
    private val log = LoggerFactory.getLogger(SeederConfig::class.java)

    override fun run(vararg args: String?) {
        val isSeedProfile = env.activeProfiles.contains("seed")
        val isSeedEnvVar = System.getenv("seed")?.toBoolean() ?: System.getenv("SEED")?.toBoolean() ?: false

        if (isSeedProfile || isSeedEnvVar) {
            log.info("Running database seeder...")
            seedUsers()
        }
    }

    private fun seedUsers() {
        if (userRepo.count() > 0) {
            log.info("Database already seeded.")
            return
        }
        val faker = Faker()
        val defaultPassword = passwordEncoder.encode("28935617Aa@")

        val requiredEmails =
            listOf(
                "kratos@gmail.com",
                "gado@gmail.com",
                "kratosgado@gmail.com",
                "admin@gmail.com",
            )

        for (email in requiredEmails) {
            if (!userRepo.existsByEmail(email)) {
                val user =
                    User().apply {
                        this.name = faker.name().fullName()
                        this.email = email
                        this.password = defaultPassword
                        this.avatarUrl = "https://i.pravatar.cc/150?u=$email"
                    }
                userRepo.save(user)
                log.info("Seeded user: $email")
            } else {
                log.info("User already exists: $email")
            }
        }

        val currentCount = userRepo.count()
        if (currentCount < 14) {
            val needed = 14 - currentCount
            for (i in 1..needed) {
                val email = faker.internet().emailAddress()
                if (!userRepo.existsByEmail(email)) {
                    val user =
                        User().apply {
                            this.name = faker.name().fullName()
                            this.email = email
                            this.password = defaultPassword
                            this.avatarUrl = "https://i.pravatar.cc/150?u=$email"
                        }
                    userRepo.save(user)
                }
            }
            log.info("Seeded $needed extra faker users.")
        }

        log.info("Seeding completed.")
    }
}
