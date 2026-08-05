package com.project.grionserver.domain.pet.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class PetLetterCreateRequest(
    @field:NotBlank(message = "쪽지 내용은 비어있을 수 없습니다.")
    @field:Size(max = 500, message = "쪽지 내용은 500자를 초과할 수 없습니다.")
    val content: String,
    val isAnonymous: Boolean
)
