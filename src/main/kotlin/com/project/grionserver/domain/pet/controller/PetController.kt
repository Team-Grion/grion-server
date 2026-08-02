package com.project.grionserver.domain.pet.controller

import com.project.grionserver.domain.pet.dto.PetAdditionalInfoRequest
import com.project.grionserver.domain.pet.dto.PetCreateRequest
import com.project.grionserver.domain.pet.dto.PetCreateResponse
import com.project.grionserver.domain.pet.dto.PetMemorialUpdateResponse
import com.project.grionserver.domain.pet.dto.PetMemorialUpdateRequest
import com.project.grionserver.domain.pet.dto.PetMemorialDetailResponse
import com.project.grionserver.domain.pet.dto.PetStatusResponse
import com.project.grionserver.domain.pet.service.PetService
import com.project.grionserver.global.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@Tag(name = "Memorial", description = "추모 공간 API")
@RestController
@RequestMapping("/memorials")
class PetController(private val petService: PetService) {

    @Operation(summary = "추모 공간 생성", description = "반려동물 사진과 정보를 받아 추모 공간을 생성합니다. " +
            "종: 강아지는 DOG, 고양이는 CAT")
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

    @Operation(summary = "추모 공간 수정", description = "추모 공간의 내용과 공개 여부를 수정합니다.")
    @PatchMapping("/me/{petId}")
    fun updateMemorial(
        @RequestHeader("Authorization") authorization: String, // Todo: 인증 로직 추후 구현
        @PathVariable petId: Long,
        @RequestBody request: PetMemorialUpdateRequest
    ): ResponseEntity<ApiResponse<PetMemorialUpdateResponse>> {
        val response = petService.updateMemorial(petId, request)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @Operation(summary = "추모 공간 추가 정보 입력", description = "이름, 생일, 기일, 추억 등 추모 공간의 추가 정보를 입력합니다. " +
            "반려 동물 이름은 공백이 불가하며 기일은 생일보다 빠를 수 없습니다.")
    @PostMapping("/{petId}/add")
    fun addPetInfo(
        @RequestHeader("Authorization") authorization: String, // Todo: 인증 로직 추후 구현
        @PathVariable petId: Long,
        @Valid @RequestBody request: PetAdditionalInfoRequest
    ): ResponseEntity<ApiResponse<Unit?>> {
        petService.addPetInfo(petId, request)
        return ResponseEntity.ok(ApiResponse.success(null))
    }

    @Operation(summary = "추모 공간 생성 상태 조회", description = "AI 이미지 생성 작업의 진행 상태를 조회합니다. " +
            "생성 중: PENDING, 생성 성공: SUCCESS, 생성 실패: FAIL")
    @GetMapping("/{petId}/status")
    fun getPetStatus(
        @RequestHeader("Authorization") authorization: String, // Todo: 인증 로직 추후 구현
        @PathVariable petId: Long
    ): ResponseEntity<ApiResponse<PetStatusResponse>> {
        val response = petService.getPetStatus(petId)
        return ResponseEntity.ok(ApiResponse.success(response))
    }


    @Operation(summary = "개인 추모 공간 상세 조회", description = "petId에 해당하는 추모 공간의 상세 정보를 조회합니다.")
    @GetMapping("/me/{petId}")
    fun getMemorialDetail(
        @RequestHeader("Authorization") authorization: String, // Todo: 인증 로직 추후 구현
        @PathVariable petId: Long
    ): ResponseEntity<ApiResponse<PetMemorialDetailResponse>> {
        val response = petService.getMemorialDetail(petId)
        return ResponseEntity.ok(ApiResponse.success(response))
    }
}
