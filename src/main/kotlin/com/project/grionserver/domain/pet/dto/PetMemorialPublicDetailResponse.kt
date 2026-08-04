package com.project.grionserver.domain.pet.dto

import java.time.LocalDate

data class PetMemorialPublicDetailResponse(
    val petId: Long,
    val petName: String?,
    val userName: String,
    val aiImageUrl: String?,
    val birthDate: LocalDate?,
    val deathDate: LocalDate?,
    val personalities: List<String>
)
