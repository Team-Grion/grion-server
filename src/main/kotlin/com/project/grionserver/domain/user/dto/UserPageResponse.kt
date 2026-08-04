package com.project.grionserver.domain.user.dto

import java.time.LocalDateTime

data class UserPageResponse(
    val userId: Long,
    val name: String,
    val profileImageUrl: String?,
    val totalCount: Long,
    val letters: List<UserLetterSummary>
)

data class UserLetterSummary(
    val letterId: Long,
    val petId: Long,
    val petName: String?,
    val isAnonymous: Boolean,
    val content: String,
    val createdAt: LocalDateTime?
)
