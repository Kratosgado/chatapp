package com.kratosgado.chatapp.chats

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "chat_rooms")
class ChatRoom(
    var name: String? = null,

    @Column(name = "is_group")
    var isGroup: Boolean = false,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: String? = null

    val createdAt: LocalDateTime = LocalDateTime.now()
}
