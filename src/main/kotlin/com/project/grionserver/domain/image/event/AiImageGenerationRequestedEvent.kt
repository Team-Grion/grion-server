package com.project.grionserver.domain.image.event

import com.project.grionserver.domain.pet.entity.Species

data class AiImageGenerationRequestedEvent(
    val taskId: Long,
    val sourceImageUrl: String,
    val species: Species,
    val breed: String,
    val personalities: List<String>,
    val background: String
)
