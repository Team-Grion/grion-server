package com.project.grionserver.domain.moderation.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class ModerationResponseDto(
    val id: String,
    val model: String,
    val results: List<ModerationResult>
)

data class ModerationResult(
    val flagged: Boolean, // 부적절 - true, 적절 - false
    val categories: Map<String, Boolean>,
    @JsonProperty("category_scores")
    val categoryScores: Map<String, Double>
)
