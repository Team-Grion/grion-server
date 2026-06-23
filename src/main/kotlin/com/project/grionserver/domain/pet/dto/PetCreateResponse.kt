package com.project.grionserver.domain.pet.dto

import java.time.LocalDate

data class PetCreateResponse(
    val id: Long,
    val name: String,
    val species: String,
    val breed: String,
    val birthday: LocalDate?,
    val deathDate: LocalDate?,
    val imageUrl: String
)
