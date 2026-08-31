package com.project.grionserver.domain.pet.dto

import java.time.LocalDate
import java.time.LocalDateTime

data class PetMemorialUpdateResponse(
    val petId: Long,
    val petName: String?,
    val aiImageUrl: String?,
    val birthDate: LocalDate?,
    val deathDate: LocalDate?,
    val letterCount: Long,
    val content: String?,
    val isPublic: Boolean,
    val updatedAt: LocalDateTime?
)
