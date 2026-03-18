package com.kratosgado.chatapp.friends

import com.kratosgado.chatapp.auth.dto.UserDto
import com.kratosgado.chatapp.friends.dto.FriendRequestDto
import com.kratosgado.chatapp.friends.dto.SendRequestDto
import com.kratosgado.chatapp.users.User
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/friends")
class FriendController(
    private val friendService: FriendService,
) {
    @PostMapping("/requests")
    fun sendRequest(
        @AuthenticationPrincipal user: User,
        @RequestBody @Valid dto: SendRequestDto,
    ): String {
        return friendService.sendRequest(user.id!!, dto.receiverId)
    }

    @GetMapping("/requests")
    fun getPendingRequests(
        @AuthenticationPrincipal user: User,
    ): List<FriendRequestDto> {
        return friendService.getPendingRequests(user.id!!)
    }

    @PutMapping("/requests/{requestId}")
    fun respondToRequest(
        @AuthenticationPrincipal user: User,
        @PathVariable requestId: String,
        @RequestParam accept: Boolean,
    ): String {
        return friendService.respondToRequest(user.id!!, requestId, accept)
    }

    @GetMapping
    fun getFriends(
        @AuthenticationPrincipal user: User,
    ): List<UserDto> {
        return friendService.getFriends(user.id!!)
    }

    @DeleteMapping("/{friendId}")
    fun removeFriend(
        @AuthenticationPrincipal user: User,
        @PathVariable friendId: String,
    ): String {
        // Here friendId is the User ID of the friend to remove
        return friendService.removeFriend(user.id!!, friendId)
    }
}
