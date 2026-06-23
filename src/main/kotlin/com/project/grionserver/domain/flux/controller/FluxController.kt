package com.project.grionserver.domain.flux.controller

import com.project.grionserver.domain.flux.dto.FluxEditResponse
import com.project.grionserver.domain.flux.service.FluxService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/flux")
class FluxController(private val fluxService: FluxService) {

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
