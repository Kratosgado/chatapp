package com.kratosgado.chatapp.chats

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "chat_rooms")
class ChatRoom(
    var name: String? = null,

    @Column(name = "is_group")
    var isGroup: Boolean = false,
    @OneToMany(
        orphanRemoval = true,
        fetch = FetchType.EAGER,
        mappedBy = "chatRoom",
        cascade = [CascadeType.PERSIST, CascadeType.MERGE]
    )
    var members: MutableSet<ChatRoomMember> = mutableSetOf(),
    @OneToMany(
        orphanRemoval = true,
        mappedBy = "chatRoom",
        fetch = FetchType.EAGER,
        cascade = [CascadeType.PERSIST, CascadeType.MERGE]
    )
    var messages: MutableSet<Message> = mutableSetOf()
) {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: String? = null

    val createdAt: LocalDateTime = LocalDateTime.now()
}
