package com.kratosgado.chatapp.friends

import com.kratosgado.chatapp.users.User
import jakarta.persistence.*
import java.time.LocalDateTime

enum class RequestStatus {
    PENDING, ACCEPTED, REJECTED
}

@Entity
@Table(name = "friend_requests")
class FriendRequest(
    @ManyToOne
    @JoinColumn(name = "sender_id")
    var sender: User,

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    var receiver: User,

    @Enumerated(EnumType.STRING)
    var status: RequestStatus = RequestStatus.PENDING,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: String? = null

    val createdAt: LocalDateTime = LocalDateTime.now()
}
