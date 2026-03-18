package com.kratosgado.chatapp.chats

import com.kratosgado.chatapp.users.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface ChatRoomRepo : JpaRepository<ChatRoom, String>

interface ChatRoomMemberRepo : JpaRepository<ChatRoomMember, String> {
    fun findByUser(user: User): List<ChatRoomMember>
    fun findByChatRoom(chatRoom: ChatRoom): List<ChatRoomMember>
    fun findByChatRoomAndUser(chatRoom: ChatRoom, user: User): ChatRoomMember?
}

interface MessageRepo : JpaRepository<Message, String> {
    fun findByChatRoomOrderBySentAtDesc(chatRoom: ChatRoom, pageable: Pageable): Page<Message>
}
