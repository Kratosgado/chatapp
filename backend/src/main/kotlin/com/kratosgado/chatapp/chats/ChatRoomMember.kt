package com.kratosgado.chatapp.chats

import com.kratosgado.chatapp.users.User
import jakarta.persistence.*

enum class MemberRole {
    OWNER, ADMIN, MEMBER
}

@Entity
@Table(name = "chat_room_members")
class ChatRoomMember(
    @ManyToOne
    @JoinColumn(name = "chat_room_id")
    var chatRoom: ChatRoom,

    @ManyToOne
    @JoinColumn(name = "user_id")
    var user: User,

    @Enumerated(EnumType.STRING)
    var role: MemberRole = MemberRole.MEMBER,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: String? = null
}
