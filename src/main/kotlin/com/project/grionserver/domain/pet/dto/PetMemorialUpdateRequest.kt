package com.project.grionserver.domain.pet.dto

data class PetMemorialUpdateRequest(
    val content: String? = null,
    val isPublic: Boolean? = null
)
