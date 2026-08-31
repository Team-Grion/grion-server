package com.project.grionserver.domain.pet.dto

data class PetInfoUpdateResponse(
    val petId: Long,
    val species: String,
    val breed: String,
    val personalities: List<String>,
    val background: String?,
    val imageStatus: String?
)
