package com.project.grionserver.domain.moderation

import com.project.grionserver.domain.moderation.dto.ModerationRequestDto
import com.project.grionserver.domain.moderation.dto.ModerationResponseDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient

@Service
class ModerationService(
    @Value("\${openai.api.key}") private val apiKey: String,
    @Value("\${openai.api.url}") private val apiUrl: String
) {
    private val restClient = RestClient.builder().build()

    fun isViolated(text: String): Boolean {
        try {
            val response = restClient.post()
                .uri(apiUrl)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer $apiKey")
                .body(ModerationRequestDto(input = text))
                .retrieve()
                .body(ModerationResponseDto::class.java)

            // flagged 추출
            return response?.results?.firstOrNull()?.flagged ?: true
        } catch (e: Exception) {
            e.printStackTrace()
            return true
        }
    }
}
