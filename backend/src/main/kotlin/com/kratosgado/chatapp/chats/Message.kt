package com.kratosgado.chatapp.chats

import com.kratosgado.chatapp.users.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "messages")
class Message(
    @ManyToOne
    @JoinColumn(name = "chat_room_id")
    var chatRoom: ChatRoom,

    @ManyToOne
    @JoinColumn(name = "sender_id")
    var sender: User,

    @Column(nullable = false)
    var content: String,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: String? = null

    val sentAt: LocalDateTime = LocalDateTime.now()
}
