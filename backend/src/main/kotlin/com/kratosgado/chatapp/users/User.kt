package com.kratosgado.chatapp.users

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: String? = null

    @Column(nullable = false)
    var name: String = ""

    @Column(unique = true, nullable = false)
    var email: String = ""

    @Column(nullable = false)
    var password: String = ""

    @Column
    var avatarUrl: String? = null

    fun getUsername(): String? = id
}
