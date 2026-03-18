package com.kratosgado.chatapp.chats

import com.kratosgado.chatapp.auth.dto.UserDto
import com.kratosgado.chatapp.chats.dto.ChatRoomDto
import com.kratosgado.chatapp.chats.dto.MessageDto
import com.kratosgado.chatapp.users.User
import com.kratosgado.chatapp.users.UserRepo
import com.kratosgado.chatapp.utils.ApiException
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChatService(
    private val chatRoomRepo: ChatRoomRepo,
    private val chatRoomMemberRepo: ChatRoomMemberRepo,
    private val messageRepo: MessageRepo,
    private val userRepo: UserRepo,
) {
    @Transactional
    fun createChat(
        userId: String,
        name: String?,
        participantIds: List<String>,
    ): ChatRoomDto {
        val user = userRepo.findById(userId).orElseThrow { ApiException.notFound("User not found") }
        val participants = userRepo.findAllById(participantIds)

        if (participants.isEmpty()) throw ApiException.badRequest("Participants required")

        // In a real app, check for existing DM
        val isGroup = participants.size > 1 || name != null

        val chatRoom = ChatRoom(name = name, isGroup = isGroup)
        chatRoomRepo.save(chatRoom)

        chatRoomMemberRepo.save(ChatRoomMember(chatRoom = chatRoom, user = user, role = MemberRole.OWNER))

        participants.forEach {
            // Avoid adding self again if included in participants list
            if (it.id != userId) {
                chatRoomMemberRepo.save(ChatRoomMember(chatRoom = chatRoom, user = it, role = MemberRole.MEMBER))
            }
        }

        return toDto(chatRoom, userId)
    }

    fun getChats(userId: String): List<ChatRoomDto> {
        val user = userRepo.findById(userId).orElseThrow { ApiException.notFound("User not found") }
        val memberships = chatRoomMemberRepo.findByUser(user)
        // Sort by last message or created at descending
        return memberships.map { toDto(it.chatRoom, userId) }
            .sortedByDescending { it.lastMessage?.sentAt ?: it.createdAt }
    }

    fun getMessages(
        userId: String,
        chatId: String,
        pageable: Pageable,
    ): List<MessageDto> {
        val user = userRepo.findById(userId).orElseThrow { ApiException.notFound("User not found") }
        val chatRoom = chatRoomRepo.findById(chatId).orElseThrow { ApiException.notFound("Chat not found") }

        if (chatRoomMemberRepo.findByChatRoomAndUser(chatRoom, user) == null) {
            throw ApiException.forbidden("Not a member")
        }

        return messageRepo.findByChatRoomOrderBySentAtDesc(chatRoom, pageable)
            .content
            .map { MessageDto.fromEntity(it) }
    }

    @Transactional
    fun sendMessage(
        userId: String,
        chatId: String,
        content: String,
    ): MessageDto {
        val user = userRepo.findById(userId).orElseThrow { ApiException.notFound("User not found") }
        val chatRoom = chatRoomRepo.findById(chatId).orElseThrow { ApiException.notFound("Chat not found") }

        if (chatRoomMemberRepo.findByChatRoomAndUser(chatRoom, user) == null) {
            throw ApiException.forbidden("Not a member")
        }

        val message = Message(chatRoom = chatRoom, sender = user, content = content)
        messageRepo.save(message)

        return MessageDto.fromEntity(message)
    }

    private fun toDto(
        chatRoom: ChatRoom,
        currentUserId: String,
    ): ChatRoomDto {
        val members = chatRoomMemberRepo.findByChatRoom(chatRoom).map { it.user }
        var displayName = chatRoom.name
        var avatarUrl: String? = null

        if (!chatRoom.isGroup) {
            // For DM, show other person's name
            val other = members.find { it.id != currentUserId }
            // If strictly 2 members or 1 (self)
            if (other != null) {
                displayName = other.name
                avatarUrl = other.avatarUrl
            } else if (members.size == 1 && members[0].id == currentUserId) {
                displayName = "Me"
                avatarUrl = members[0].avatarUrl
            }
        }
        
        if (displayName == null) {
             displayName = members.take(3).joinToString(", ") { it.name } + if (members.size > 3) "..." else ""
        }

        val lastMessagePage = messageRepo.findByChatRoomOrderBySentAtDesc(chatRoom, Pageable.ofSize(1))
        val lastMessage = if (lastMessagePage.hasContent()) MessageDto.fromEntity(lastMessagePage.content[0]) else null

        return ChatRoomDto(
            id = chatRoom.id!!,
            name = displayName ?: "Chat",
            isGroup = chatRoom.isGroup,
            avatarUrl = avatarUrl,
            lastMessage = lastMessage,
            members = members.map { UserDto.fromEntity(it) },
            createdAt = chatRoom.createdAt,
        )
    }
}
