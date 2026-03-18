package com.kratosgado.chatapp.users

import com.kratosgado.chatapp.auth.dto.UserDto
import com.kratosgado.chatapp.users.dto.UpdateProfileDto
import com.kratosgado.chatapp.utils.ApiException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepo: UserRepo,
) {
    fun getUserById(id: String): UserDto {
        val user = userRepo.findById(id).orElseThrow { ApiException.notFound("User not found") }
        return UserDto.fromEntity(user)
    }

    @Transactional
    fun updateProfile(
        id: String,
        dto: UpdateProfileDto,
    ): UserDto {
        val user = userRepo.findById(id).orElseThrow { ApiException.notFound("User not found") }
        user.name = dto.name
        user.avatarUrl = dto.avatarUrl
        return UserDto.fromEntity(userRepo.save(user))
    }

    fun searchUsers(query: String): List<UserDto> {
        val users = userRepo.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query)
        return users.map { UserDto.fromEntity(it) }
    }
}
