package com.kratosgado.chatapp.users

import com.kratosgado.chatapp.auth.dto.UserDto
import com.kratosgado.chatapp.users.dto.UpdateProfileDto
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
) {
    @GetMapping("/me")
    fun getCurrentUser(
        @AuthenticationPrincipal userId: String,
    ): UserDto = userService.getUserById(userId)

    @PutMapping("/me")
    fun updateProfile(
        @AuthenticationPrincipal userId: String,
        @RequestBody dto: UpdateProfileDto,
    ): UserDto = userService.updateProfile(userId, dto)

    @GetMapping("/{id}")
    fun getUser(
        @PathVariable id: String,
    ): UserDto = userService.getUserById(id)

    @GetMapping
    fun searchUsers(
        @RequestParam query: String,
    ): List<UserDto> = userService.searchUsers(query)
}
