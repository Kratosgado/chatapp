package com.kratosgado.chatapp.friends

import com.kratosgado.chatapp.users.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface FriendRepo : JpaRepository<FriendRequest, String> {
    fun findByReceiverAndStatus(receiver: User, status: RequestStatus): List<FriendRequest>
    
    fun findBySenderAndReceiver(sender: User, receiver: User): FriendRequest?

    @Query("SELECT r FROM FriendRequest r WHERE (r.sender = :user OR r.receiver = :user) AND r.status = com.kratosgado.chatapp.friends.RequestStatus.ACCEPTED")
    fun findAcceptedFriendships(user: User): List<FriendRequest>
}
