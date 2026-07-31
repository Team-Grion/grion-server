package com.project.grionserver.domain.pet.dto

import java.time.LocalDate

data class PetMemorialDetailResponse(
    val petId: Long,
    val petName: String?,
    val aiImageUrl: String?,
    val birthDate: LocalDate?,
    val deathDate: LocalDate?,
    val letterCount: Long,
    val content: String?,
    val isPublic: Boolean
)
