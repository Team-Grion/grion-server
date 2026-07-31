package com.project.grionserver.domain.pet.controller

import com.project.grionserver.domain.pet.dto.PetCreateRequest
import com.project.grionserver.domain.pet.dto.PetCreateResponse
import com.project.grionserver.domain.pet.service.PetService
import com.project.grionserver.global.response.ApiResponse
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/memorials")
class PetController(private val petService: PetService) {

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createPet(
        @RequestParam userId: Long,
        @RequestPart("petImageUrl") petImage: MultipartFile,
        @RequestParam species: String,
        @RequestParam breed: String,
        @RequestParam personalities: List<String>,
        @RequestParam background: String
    ): ResponseEntity<ApiResponse<PetCreateResponse>> {
        val request = PetCreateRequest(
            userId = userId,
            species = species,
            breed = breed,
            personalities = personalities,
            background = background
        )
        val response = petService.createPet(petImage, request)
        return ResponseEntity.ok(ApiResponse.success(response))
    }
}
