package com.project.grionserver.domain.pet.dto

import java.time.LocalDateTime

data class PetLetterCreateResponse(
    val letterId: Long,
    val createdAt: LocalDateTime?
)
