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

        val chatMembers = participants.map { ChatRoomMember(chatRoom, it, MemberRole.MEMBER) }.toMutableSet()
        chatMembers.add(ChatRoomMember(chatRoom = chatRoom, user, MemberRole.OWNER))

        chatRoomMemberRepo.saveAll(chatMembers)
        chatRoom.members = chatMembers
        return toDto(chatRoom, userId)
    }

    fun getChats(userId: String): List<ChatRoomDto> {
        val chats = chatRoomRepo.findByMembersUserId(userId)
        // Sort by last message or created at descending
        return chats
            .map { toDto(it, userId) }
    }

    fun getMessages(
        userId: String,
        chatId: String,
        pageable: Pageable,
    ): List<MessageDto> {
        var user = userRepo.getReferenceById(userId)
        var chatRoom = chatRoomRepo.getReferenceById(chatId)

        if (!chatRoomMemberRepo.existsByUserAndChatRoom(user, chatRoom)) {
            throw ApiException.forbidden("Not a member")
        }

        return messageRepo
            .findByChatRoomIdOrderBySentAtDesc(chatId, pageable)
            .content
            .map { MessageDto.fromEntity(it) }
    }

    @Transactional
    fun sendMessage(
        userId: String,
        chatId: String,
        content: String,
    ): MessageDto {
        val user = userRepo.getReferenceById(userId)
        val chatRoom = chatRoomRepo.getReferenceById(chatId)

        if (!chatRoomMemberRepo.existsByUserAndChatRoom(user, chatRoom)) {
            throw ApiException.forbidden("Not a member")
        }

        val message = Message(chatRoom, user, content)
        messageRepo.save(message)

        return MessageDto.fromEntity(message)
    }

    private fun toDto(
        chatRoom: ChatRoom,
        currentUserId: String,
    ): ChatRoomDto {
        val members = chatRoom.members.map { it.user }
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

        val lastMessagePage = chatRoom.messages.lastOrNull()
        val lastMessage = if (lastMessagePage != null) MessageDto.fromEntity(lastMessagePage) else null

        return ChatRoomDto(
            id = chatRoom.id!!,
            name = displayName,
            isGroup = chatRoom.isGroup,
            avatarUrl = avatarUrl,
            lastMessage = lastMessage,
            members = members.map { UserDto.fromEntity(it) },
            createdAt = chatRoom.createdAt,
        )
    }
}
