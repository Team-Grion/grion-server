package com.project.grionserver.domain.pet.dto

import java.time.LocalDate

data class PetAdditionalInfoRequest(
    val petName: String,
    val birthDate: LocalDate,
    val deathDate: LocalDate,
    val memory: String? = null
)
