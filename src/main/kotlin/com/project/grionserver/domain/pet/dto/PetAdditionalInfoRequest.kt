package com.project.grionserver.domain.pet.dto

import jakarta.validation.constraints.NotBlank
import java.time.LocalDate

data class PetAdditionalInfoRequest(
    @field:NotBlank(message = "반려동물 이름은 비어있을 수 없습니다.")
    val petName: String,
    val birthDate: LocalDate,
    val deathDate: LocalDate,
    val memory: String? = null
)
