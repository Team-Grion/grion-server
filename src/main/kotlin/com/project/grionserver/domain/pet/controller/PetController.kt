package com.project.grionserver.domain.pet.controller

import com.project.grionserver.domain.pet.dto.PetAdditionalInfoRequest
import com.project.grionserver.domain.pet.dto.PetCreateRequest
import com.project.grionserver.domain.pet.dto.PetCreateResponse
import com.project.grionserver.domain.pet.dto.PetMemorialDetailResponse
import com.project.grionserver.domain.pet.dto.PetStatusResponse
import com.project.grionserver.domain.pet.service.PetService
import com.project.grionserver.global.response.ApiResponse
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/memorials")
class PetController(private val petService: PetService) {

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createPet(
        @RequestHeader("Authorization") authorization: String, // Todo: 인증 로직 추후 구현
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

    @PostMapping("/{petId}/add")
    fun addPetInfo(
        @RequestHeader("Authorization") authorization: String, // Todo: 인증 로직 추후 구현
        @PathVariable petId: Long,
        @Valid @RequestBody request: PetAdditionalInfoRequest
    ): ResponseEntity<ApiResponse<Unit?>> {
        petService.addPetInfo(petId, request)
        return ResponseEntity.ok(ApiResponse.success(null))
    }

    @GetMapping("/{petId}/status")
    fun getPetStatus(
        @RequestHeader("Authorization") authorization: String, // Todo: 인증 로직 추후 구현
        @PathVariable petId: Long
    ): ResponseEntity<ApiResponse<PetStatusResponse>> {
        val response = petService.getPetStatus(petId)
        return ResponseEntity.ok(ApiResponse.success(response))
    }


    @GetMapping("/me/{petId}")
    fun getMemorialDetail(
        @RequestHeader("Authorization") authorization: String, // Todo: 인증 로직 추후 구현
        @PathVariable petId: Long
    ): ResponseEntity<ApiResponse<PetMemorialDetailResponse>> {
        val response = petService.getMemorialDetail(petId)
    }
}
