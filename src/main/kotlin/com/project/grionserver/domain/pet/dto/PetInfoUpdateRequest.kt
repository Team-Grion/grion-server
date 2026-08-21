package com.project.grionserver.domain.pet.dto

data class PetInfoUpdateRequest(
    val breed: String? = null,
    val personalities: List<String>? = null,
    val background: String? = null
)
