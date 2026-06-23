package com.project.grionserver.domain.pet.dto

import java.time.LocalDate

data class PetCreateRequest(
    val userId: Long,
    val name: String,
    val species: String,
    val breed: String,
    val birthday: LocalDate?,
    val deathDate: LocalDate?,
    val characteristics: String?,
    val memories: String?,
    val backgroundText: String?,
)
