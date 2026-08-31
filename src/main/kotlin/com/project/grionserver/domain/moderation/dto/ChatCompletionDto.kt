package com.project.grionserver.domain.moderation.dto

data class ChatMessage(
    val role: String,
    val content: String
)

data class ChatCompletionRequestDto(
    val messages: List<ChatMessage>,
    val model: String = "gpt-4o-mini",
    val temperature: Double = 0.0
)

data class ChatCompletionResponseDto(
    val choices: List<ChatChoice>
)

data class ChatChoice(
    val message: ChatMessage
)
