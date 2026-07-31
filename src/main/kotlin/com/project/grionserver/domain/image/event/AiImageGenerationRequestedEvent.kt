package com.project.grionserver.domain.image.event

data class AiImageGenerationRequestedEvent(
    val taskId: Long,
    val sourceImageUrl: String,
    val prompt: String
)
