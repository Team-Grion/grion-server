package com.project.grionserver.domain.flux.service

import com.project.grionserver.domain.image.entity.AiImageTask
import com.project.grionserver.domain.image.entity.PetImage
import com.project.grionserver.domain.image.repository.AiImageTaskRepository
import com.project.grionserver.domain.image.repository.PetImageRepository
import com.project.grionserver.domain.pet.repository.PetRepository
import com.project.grionserver.global.service.FalStorageService
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@Service
@Transactional
class FluxService(
    @Value("\${fal.api.key}") private val fluxKey: String,
    private val falStorageService: FalStorageService,
    private val petRepository: PetRepository,
    private val aiImageTaskRepository: AiImageTaskRepository,
    private val petImageRepository: PetImageRepository
) {
    private val restTemplate = RestTemplate()

    fun uploadAndEdit(image: MultipartFile, prompt: String, petId: Long): String {
        val pet = petRepository.findById(petId)
            .orElseThrow { IllegalArgumentException("반려동물을 찾을 수 없습니다.") }

        val imageUrl = falStorageService.upload(image)

        val task = aiImageTaskRepository.save(
            AiImageTask(
                pet = pet,
                requestId = UUID.randomUUID().toString(),
                status = "PENDING"
            )
        )

        return try {
            val editedUrl = editImage(prompt, imageUrl)
                ?: throw RuntimeException("이미지 편집 결과를 받지 못했습니다.")

            task.status = "SUCCESS"
            task.resultUrl = editedUrl

            petImageRepository.save(
                PetImage(pet = pet, imageUrl = editedUrl, isMain = false)
            )

            editedUrl
        } catch (e: Exception) {
            task.status = "FAIL"
            task.failureReason = e.message
            throw e
        }
    }

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
