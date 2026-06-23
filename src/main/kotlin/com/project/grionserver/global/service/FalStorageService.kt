package com.project.grionserver.global.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.multipart.MultipartFile

@Service
class FalStorageService(
    @Value("\${fal.api.key}") private val falApiKey: String
) {
    private val restTemplate = RestTemplate()

    fun upload(file: MultipartFile): String {
        val authHeaders = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Key $falApiKey")
        }

        // 1단계: presigned URL 요청
        val initiateBody = mapOf(
            "content_type" to (file.contentType ?: "image/jpeg"),
            "file_name" to (file.originalFilename ?: "image.jpg")
        )
        val initiateResponse = restTemplate.postForEntity(
            "https://rest.alpha.fal.ai/storage/upload/initiate",
            HttpEntity(initiateBody, authHeaders),
            Map::class.java
        )

        @Suppress("UNCHECKED_CAST")
        val body = initiateResponse.body as? Map<String, Any>
            ?: throw RuntimeException("fal.ai 스토리지 업로드 초기화 실패")

        val uploadUrl = body["upload_url"] as? String
            ?: throw RuntimeException("업로드 URL 획득 실패")
        val storageUrl = body["file_url"] as? String
            ?: throw RuntimeException("스토리지 URL 획득 실패")

        // 2단계: presigned URL로 파일 업로드
        val uploadHeaders = HttpHeaders().apply {
            contentType = MediaType.parseMediaType(file.contentType ?: "image/jpeg")
        }
        restTemplate.exchange(
            uploadUrl,
            HttpMethod.PUT,
            HttpEntity(file.bytes, uploadHeaders),
            String::class.java
        )

        return storageUrl
    }
}
