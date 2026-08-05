package com.project.grionserver.domain.pet.dto

import java.time.LocalDate

data class PetMemorialPublicListResponse(
    val todaySummary: PetMemorialPublicTodaySummary,
    val content: List<PetMemorialPublicSummary>
)

data class PetMemorialPublicTodaySummary(
    val memorialCount: Long,
    val messageCount: Long
)

data class PetMemorialPublicSummary(
    val petId: Long,
    val petName: String?,
    val aiImageUrl: String?,
    val birthDate: LocalDate?,
    val deathDate: LocalDate?,
    val introduction: String?,
    val personalities: List<String>,
    val todayMessageCount: Long,
    val totalMessageCount: Long
)
