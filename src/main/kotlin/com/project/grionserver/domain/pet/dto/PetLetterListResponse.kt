package com.project.grionserver.domain.pet.dto

import java.time.LocalDateTime

data class PetLetterListResponse(
    val petId: Long,
    val letters: List<PetLetterSummary>
)

data class PetLetterSummary(
    val letterId: Long,
    val senderName: String,
    val content: String,
    val createdAt: LocalDateTime?
)
