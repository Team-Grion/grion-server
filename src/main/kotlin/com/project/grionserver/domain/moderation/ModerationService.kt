package com.project.grionserver.domain.moderation

import com.project.grionserver.domain.moderation.dto.ChatCompletionRequestDto
import com.project.grionserver.domain.moderation.dto.ChatCompletionResponseDto
import com.project.grionserver.domain.moderation.dto.ChatMessage
import com.project.grionserver.domain.moderation.dto.ModerationRequestDto
import com.project.grionserver.domain.moderation.dto.ModerationResponseDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient

@Service
class ModerationService(
    @Value("\${openai.api.key}") private val apiKey: String,
    @Value("\${openai.api.url}") private val apiUrl: String,
    @Value("\${openai.api.chat-url}") private val chatApiUrl: String
) {
    private val restClient = RestClient.builder().build()

    fun isViolated(text: String): Boolean {
        if (isFlaggedByModeration(text)) {
            return true
        }

        return isDisrespectfulTone(text)
    }

    private fun isFlaggedByModeration(text: String): Boolean {
        return try {
            val response = restClient.post()
                .uri(apiUrl)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer $apiKey")
                .body(ModerationRequestDto(input = text))
                .retrieve()
                .body(ModerationResponseDto::class.java)

            response?.results?.firstOrNull()?.flagged ?: true
        } catch (e: Exception) {
            e.printStackTrace()
            true
        }
    }

    private fun isDisrespectfulTone(text: String): Boolean {
        return try {
            val response = restClient.post()
                .uri(chatApiUrl)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer $apiKey")
                .body(
                    ChatCompletionRequestDto(
                        messages = listOf(
                            ChatMessage(role = "system", content = TONE_CHECK_PROMPT),
                            ChatMessage(role = "user", content = text)
                        )
                    )
                )
                .retrieve()
                .body(ChatCompletionResponseDto::class.java)

            val verdict = response?.choices?.firstOrNull()?.message?.content?.trim()
            !verdict.equals("false", ignoreCase = true)
        } catch (e: Exception) {
            e.printStackTrace()
            true
        }
    }

    companion object {
        private const val TONE_CHECK_PROMPT =
            "너는 반려동물 추모 공간에 남기는 쪽지가 적절한지 판단하는 검수자야. " +
                "애도의 뜻에 어긋나거나 무례하거나 부정적인 감정 표현(예: 짜증, 싫음, 외모 비하 등)이 담겨 있으면 부적절해. " +
                "단, 그리움/사랑/슬픔처럼 반려동물을 향한 애틋한 감정 표현은 표현이 강하거나 다급해 보여도 절대 부적절이 아니야. " +
                "예시(부적절 아님, false): \"보고싶어\", \"빨리 보고싶다\", \"많이 그리워\", \"사랑해 언제나\", \"천국에서 편히 쉬렴\", \"함께한 시간 행복했어\". " +
                "예시(부적절, true): \"보기만해도 짜증나\", \"별로였어\", \"못생겼었지\", \"다시 보고 싶지 않아\". " +
                "주어진 문장이 부적절하면 정확히 \"true\"라고만, 적절하면 정확히 \"false\"라고만 답해. 다른 말은 절대 하지 마."
    }
}
