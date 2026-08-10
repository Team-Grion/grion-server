package com.project.grionserver.domain.user.service

import com.project.grionserver.domain.message.repository.MessageRepository
import com.project.grionserver.domain.user.dto.UserLetterSummary
import com.project.grionserver.domain.user.dto.UserPageResponse
import com.project.grionserver.domain.user.repository.UserRepository
import com.project.grionserver.global.exception.NotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val messageRepository: MessageRepository
) {
    fun getMyPage(userId: Long): UserPageResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { NotFoundException("사용자를 찾을 수 없습니다.") }

        val letters = messageRepository.findAllBySenderOrderByCreatedAtDesc(user).map { message ->
            UserLetterSummary(
                letterId = message.id,
                petId = message.pet.id,
                petName = message.pet.name,
                isAnonymous = message.isAnonymous,
                content = message.content,
                isPetPublic = message.pet.isShared,
                createdAt = message.createdAt
            )
        }

        return UserPageResponse(
            userId = user.id,
            name = user.nickname,
            profileImageUrl = user.profileImageUrl,
            totalCount = letters.size.toLong(),
            letters = letters
        )
    }
}
