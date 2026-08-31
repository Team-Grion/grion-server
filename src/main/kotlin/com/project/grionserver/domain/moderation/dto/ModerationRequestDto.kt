package com.project.grionserver.domain.moderation.dto

data class ModerationRequestDto(
    val input: String,
    val model: String = "omni-moderation-latest"
)
