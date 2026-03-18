package com.kratosgado.chatapp.users

import org.springframework.data.jpa.repository.JpaRepository

interface UserRepo : JpaRepository<User, String> {
    fun existsByEmail(email: String): Boolean
    fun findByEmail(email: String): User?
    fun findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(name: String, email: String): List<User>
}
