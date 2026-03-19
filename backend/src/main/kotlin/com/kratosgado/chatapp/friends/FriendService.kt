package com.kratosgado.chatapp.friends

import com.kratosgado.chatapp.auth.dto.UserDto
import com.kratosgado.chatapp.friends.dto.FriendRequestDto
import com.kratosgado.chatapp.users.UserRepo
import com.kratosgado.chatapp.utils.ApiException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FriendService(
    private val friendRepo: FriendRepo,
    private val userRepo: UserRepo,
) {
    @Transactional
    fun sendRequest(
        senderId: String,
        receiverId: String,
    ): String {
        if (senderId == receiverId) throw ApiException.badRequest("Cannot add self")

        val sender = userRepo.findById(senderId).orElseThrow { ApiException.notFound("Sender not found") }
        val receiver = userRepo.findById(receiverId).orElseThrow { ApiException.notFound("Receiver not found") }

        // Check if request already exists (either direction)
        val existing =
            friendRepo.findBySenderAndReceiver(sender, receiver)
                ?: friendRepo.findBySenderAndReceiver(receiver, sender)

        if (existing != null) {
            if (existing.status == RequestStatus.ACCEPTED) throw ApiException.badRequest("Already friends")
            if (existing.status == RequestStatus.PENDING) throw ApiException.badRequest("Request already pending")
            // If rejected, allow resend - create new or update existing?
            // For simplicity, update existing if present, else create new
            if (existing.status == RequestStatus.REJECTED) {
                existing.status = RequestStatus.PENDING
                existing.sender = sender // Reset sender in case it was reversed
                existing.receiver = receiver
                friendRepo.save(existing)
                return "Request sent"
            }
        }

        val request = FriendRequest(sender = sender, receiver = receiver)
        friendRepo.save(request)
        return "Request sent"
    }

    @Transactional
    fun respondToRequest(
        userId: String,
        requestId: String,
        accept: Boolean,
    ): String {
        val request = friendRepo.findById(requestId).orElseThrow { ApiException.notFound("Request not found") }

        if (request.receiver.id != userId) throw ApiException.forbidden("Not your request")
        if (request.status != RequestStatus.PENDING) throw ApiException.badRequest("Request not pending")

        if (accept) {
            request.status = RequestStatus.ACCEPTED
            friendRepo.save(request)
            return "Accepted"
        } else {
            friendRepo.delete(request)
            return "Rejected"
        }
    }

    fun getPendingRequests(userId: String): List<FriendRequestDto> {
        val user = userRepo.findById(userId).orElseThrow { ApiException.notFound("User not found") }
        return friendRepo.findByReceiverAndStatus(user, RequestStatus.PENDING).map {
            FriendRequestDto(it.id!!, UserDto.fromEntity(it.sender), it.createdAt)
        }
    }

    fun getFriends(userId: String): List<UserDto> {
        val user = userRepo.findById(userId).orElseThrow { ApiException.notFound("User not found") }
        val friendships = friendRepo.findAcceptedFriendships(user)

        return friendships.map {
            val friend = if (it.sender.id == userId) it.receiver else it.sender
            UserDto.fromEntity(friend)
        }
    }

    fun removeFriend(
        userId: String,
        friendId: String,
    ): String {
        val user = userRepo.findById(userId).orElseThrow { ApiException.notFound("User not found") }
        val friend = userRepo.findById(friendId).orElseThrow { ApiException.notFound("Friend not found") }

        val friendship =
            friendRepo.findBySenderAndReceiver(user, friend)
                ?: friendRepo.findBySenderAndReceiver(friend, user)
                ?: throw ApiException.notFound("Friendship not found")

        if (friendship.status != RequestStatus.ACCEPTED) throw ApiException.badRequest("Not friends")

        friendRepo.delete(friendship)
        return "Friend removed"
    }
}
