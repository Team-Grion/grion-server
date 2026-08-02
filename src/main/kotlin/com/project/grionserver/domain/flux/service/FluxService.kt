package com.project.grionserver.domain.flux.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class FluxService(
    @Value("\${fal.api.key}") private val fluxKey: String
) {
    private val restTemplate = RestTemplate()

    fun editImage(prompt: String, imageUrl: String): String? {
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Key $fluxKey")
        }

        val requestBody = mapOf(
            "prompt" to prompt,
            "image_urls" to listOf(imageUrl)
        )

        return try {
            val response = restTemplate.postForEntity(
                "https://fal.run/fal-ai/flux-2-pro/edit",
                HttpEntity(requestBody, headers),
                Map::class.java
            )
            val responseBody = response.body as? Map<String, Any>

            @Suppress("UNCHECKED_CAST")
            val images = responseBody?.get("images") as? List<Map<String, Any>>
            images?.firstOrNull()?.get("url") as? String

        } catch (e: Exception) {
            println("Fal.ai API 호출 중 에러 발생: ${e.message}")
            throw RuntimeException("이미지 편집에 실패했습니다.")
        }
    }
}
