package com.kratosgado.chatapp.chats

import com.kratosgado.chatapp.users.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ChatRoomRepo : JpaRepository<ChatRoom, String> {


    @Query("SELECT c FROM ChatRoom c LEFT JOIN c.members m LEFT JOIN Message msg ON msg.chatRoom = c WHERE m.user.id = :userId AND (msg.sentAt = (SELECT MAX(m2.sentAt) FROM Message m2 WHERE m2.chatRoom = c) OR msg IS NULL)")
    fun findByMembersUserId(userId: String): List<ChatRoom>
}

interface ChatRoomMemberRepo : JpaRepository<ChatRoomMember, String> {
    fun findByUserId(userId: String): List<ChatRoomMember>
    fun findByChatRoomId(chatRoomId: String): List<ChatRoomMember>

    fun existsByUserIdAndChatRoomId(userId: String, chatRoomId: String): Boolean

}

interface MessageRepo : JpaRepository<Message, String> {
    fun findByChatRoomIdOrderBySentAtDesc(
        chatRoomId: String,
        pageable: Pageable,
    ): Page<Message>
}
