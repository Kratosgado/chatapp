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
        @AuthenticationPrincipal userId: String,
        @RequestBody @Valid dto: SendRequestDto,
    ): String = friendService.sendRequest(userId, dto.receiverId)

    @GetMapping("/requests")
    fun getPendingRequests(
        @AuthenticationPrincipal userId: String,
    ): List<FriendRequestDto> = friendService.getPendingRequests(userId)

    @PutMapping("/requests/{requestId}")
    fun respondToRequest(
        @AuthenticationPrincipal userId: String,
        @PathVariable requestId: String,
        @RequestParam accept: Boolean,
    ): String = friendService.respondToRequest(userId, requestId, accept)

    @GetMapping
    fun getFriends(
        @AuthenticationPrincipal userId: String,
    ): List<UserDto> = friendService.getFriends(userId)

    @DeleteMapping("/{friendId}")
    fun removeFriend(
        @AuthenticationPrincipal userId: String,
        @PathVariable friendId: String,
    ): String = friendService.removeFriend(userId, friendId)
}
