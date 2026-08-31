package com.project.grionserver.domain.pet.dto

data class PetCreateRequest(
    val userId: Long,
    val species: String,
    val breed: String,
    val personalities: List<String>,
    val background: String
)
