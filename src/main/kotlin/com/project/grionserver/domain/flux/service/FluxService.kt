package com.project.grionserver.domain.flux.service

import com.project.grionserver.global.service.FalStorageService
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.multipart.MultipartFile

@Service
class FluxService(
    @Value("\${fal.api.key}") private val fluxKey: String,
    private val falStorageService: FalStorageService
) {
    private val restTemplate = RestTemplate()

    fun uploadAndEdit(image: MultipartFile, prompt: String): String {
        val imageUrl = falStorageService.upload(image)
        return editImage(prompt, imageUrl)
            ?: throw RuntimeException("이미지 편집 결과를 받지 못했습니다.")
    }

    fun editImage(prompt: String, imageUrl: String): String? {
        val falApiUrl = "https://fal.run/fal-ai/flux-2-pro/edit"

        // 1. HTTP 헤더 설정
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Key $fluxKey")
        }

        // 2. 요청 바디 세팅
        val requestBody = mapOf(
            "prompt" to prompt,
            "image_urls" to listOf(imageUrl)
        )

        // 3. 요청 객체 생성
        val requestEntity = HttpEntity(requestBody, headers)

        return try {
            // 4. POST 요청
            val response = restTemplate.postForEntity(falApiUrl, requestEntity, Map::class.java)
            val responseBody = response.body as? Map<String, Any>

            // 5. 안전한 타입 캐스팅(as?)으로 이미지 URL만 추출
            @Suppress("UNCHECKED_CAST")
            val images = responseBody?.get("images") as? List<Map<String, Any>>
            images?.firstOrNull()?.get("url") as? String

        } catch (e: Exception) {
            println("Fal.ai API 호출 중 에러 발생: ${e.message}")
            throw RuntimeException("이미지 편집에 실패했습니다.")
        }
    }
}
