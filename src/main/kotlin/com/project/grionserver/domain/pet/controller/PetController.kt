package com.project.grionserver.domain.pet.controller

import com.project.grionserver.domain.pet.dto.PetAdditionalInfoRequest
import com.project.grionserver.domain.pet.dto.PetCreateRequest
import com.project.grionserver.domain.pet.dto.PetCreateResponse
import com.project.grionserver.domain.pet.dto.PetLetterListResponse
import com.project.grionserver.domain.pet.dto.PetLetterCreateRequest
import com.project.grionserver.domain.pet.dto.PetLetterCreateResponse
import com.project.grionserver.domain.pet.dto.PetPrivateMemorialListResponse
import com.project.grionserver.domain.pet.dto.PetMemorialUpdateResponse
import com.project.grionserver.domain.pet.dto.PetMemorialUpdateRequest
import com.project.grionserver.domain.pet.dto.PetMemorialDetailResponse
import com.project.grionserver.domain.pet.dto.PetMemorialPublicDetailResponse
import com.project.grionserver.domain.pet.dto.PetMemorialPublicListResponse
import com.project.grionserver.domain.pet.dto.PetStatusResponse
import com.project.grionserver.domain.pet.service.PetService
import com.project.grionserver.global.response.ApiResponse
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/memorials")
class PetController(private val petService: PetService) {

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createPet(
        @AuthenticationPrincipal userId: Long,
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

    @PatchMapping("/me/{petId}")
    fun updateMemorial(
        @AuthenticationPrincipal userId: Long,
        @PathVariable petId: Long,
        @RequestBody request: PetMemorialUpdateRequest
    ): ResponseEntity<ApiResponse<PetMemorialUpdateResponse>> {
        val response = petService.updateMemorial(petId, userId, request)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @PostMapping("/{petId}/add")
    fun addPetInfo(
        @AuthenticationPrincipal userId: Long,
        @PathVariable petId: Long,
        @Valid @RequestBody request: PetAdditionalInfoRequest
    ): ResponseEntity<ApiResponse<Unit?>> {
        petService.addPetInfo(petId, userId, request)
        return ResponseEntity.ok(ApiResponse.success(null))
    }

    @GetMapping("/{petId}/status")
    fun getPetStatus(
        @AuthenticationPrincipal userId: Long,
        @PathVariable petId: Long
    ): ResponseEntity<ApiResponse<PetStatusResponse>> {
        val response = petService.getPetStatus(petId, userId)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @GetMapping("/me")
    fun getMyMemorials(
        @AuthenticationPrincipal userId: Long
    ): ResponseEntity<ApiResponse<PetPrivateMemorialListResponse>> {
        val response = petService.getMyMemorials(userId)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @GetMapping("/me/{petId}")
    fun getMemorialDetail(
        @AuthenticationPrincipal userId: Long,
        @PathVariable petId: Long
    ): ResponseEntity<ApiResponse<PetMemorialDetailResponse>> {
        val response = petService.getMemorialDetail(petId, userId)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @GetMapping("/me/{petId}/letters")
    fun getLetters(
        @AuthenticationPrincipal userId: Long,
        @PathVariable petId: Long
    ): ResponseEntity<ApiResponse<PetLetterListResponse>> {
        val response = petService.getLetters(petId, userId)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @GetMapping("/public")
    fun getPublicMemorials(
        @RequestParam(defaultValue = "ALL") species: String
    ): ResponseEntity<ApiResponse<PetMemorialPublicListResponse>> {
        val response = petService.getPublicMemorials(species)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @GetMapping("/public/{petId}")
    fun getPublicMemorialDetail(
        @PathVariable petId: Long
    ): ResponseEntity<ApiResponse<PetMemorialPublicDetailResponse>> {
        val response = petService.getPublicMemorialDetail(petId)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @PostMapping("/public/{petId}/letter")
    fun createLetter(
        @AuthenticationPrincipal userId: Long,
        @PathVariable petId: Long,
        @Valid @RequestBody request: PetLetterCreateRequest
    ): ResponseEntity<ApiResponse<PetLetterCreateResponse>> {
        val response = petService.createLetter(petId, userId, request)
        return ResponseEntity.ok(ApiResponse.success(response))
    }
}
