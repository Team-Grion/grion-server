package com.project.grionserver.domain.pet.dto

data class PetPrivateMemorialListResponse(
    val memorials: List<PetPrivateMemorialSummary>
)

data class PetPrivateMemorialSummary(
    val petId: Long,
    val petName: String?,
    val aiImageUrl: String?
)
