package com.project.grionserver.domain.pet.dto

data class PetLetterCreateRequest(
    val content: String,
    val isAnonymous: Boolean
)
