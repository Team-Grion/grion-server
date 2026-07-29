package com.project.grionserver.domain.flux.controller

import com.project.grionserver.domain.flux.dto.FluxEditResponse
import com.project.grionserver.domain.flux.service.FluxService
import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@Hidden
@Tag(name = "Flux", description = "AI 이미지 편집 API")
@RestController
@RequestMapping("/flux")
class FluxController(private val fluxService: FluxService) {

    @Operation(summary = "이미지 편집", description = "반려동물 이미지를 AI로 편집합니다.")
    @PostMapping("/edit", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun editImage(
        @RequestPart("image") image: MultipartFile,
        @RequestPart("prompt") prompt: String,
        @RequestParam petId: Long
    ): ResponseEntity<FluxEditResponse> {
        val editedUrl = fluxService.uploadAndEdit(image, prompt, petId)
        return ResponseEntity.ok(FluxEditResponse(editedUrl))
    }
}
