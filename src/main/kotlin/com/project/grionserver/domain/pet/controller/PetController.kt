package com.project.grionserver.domain.pet.controller

import com.project.grionserver.domain.pet.dto.PetAdditionalInfoRequest
import com.project.grionserver.domain.pet.dto.PetCreateRequest
import com.project.grionserver.domain.pet.dto.PetCreateResponse
import com.project.grionserver.domain.pet.dto.PetInfoUpdateRequest
import com.project.grionserver.domain.pet.dto.PetInfoUpdateResponse
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
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirements
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
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

    @Operation(summary = "추모 공간 수정", description = "추모 공간의 내용과 공개 여부를 수정합니다.")
    @PatchMapping("/me/{petId}")
    fun updateMemorial(
        @AuthenticationPrincipal userId: Long,
        @PathVariable petId: Long,
        @RequestBody request: PetMemorialUpdateRequest
    ): ResponseEntity<ApiResponse<PetMemorialUpdateResponse>> {
        val response = petService.updateMemorial(petId, userId, request)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @Operation(summary = "추모 공간 생성 정보 수정", description = "품종, 성격, 배경, 원본 사진을 수정합니다. " +
            "\n종(species)은 수정할 수 없습니다. petImageUrl을 첨부하지 않으면 기존에 저장된 원본 사진으로 재생성합니다. " +
            "변경된 필드가 없으면 이미지 재생성이 이루어지지 않으며, 이 경우 imageStatus는 null로 반환됩니다.")
    @PatchMapping("/me/{petId}/info", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun updatePetInfo(
        @AuthenticationPrincipal userId: Long,
        @PathVariable petId: Long,
        @RequestPart("petImageUrl", required = false) petImage: MultipartFile?,
        @RequestParam(required = false) breed: String?,
        @RequestParam(required = false) personalities: List<String>?,
        @RequestParam(required = false) background: String?
    ): ResponseEntity<ApiResponse<PetInfoUpdateResponse>> {
        val request = PetInfoUpdateRequest(
            breed = breed,
            personalities = personalities,
            background = background
        )
        val response = petService.updatePetInfo(petId, userId, petImage, request)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @Operation(summary = "추모 공간 추가 정보 입력", description = "이름, 생일, 기일, 추억 등 추모 공간의 추가 정보를 입력합니다. " +
            "반려 동물 이름은 공백이 불가하며 기일은 생일보다 빠를 수 없습니다.")
    @PostMapping("/{petId}/add")
    fun addPetInfo(
        @AuthenticationPrincipal userId: Long,
        @PathVariable petId: Long,
        @Valid @RequestBody request: PetAdditionalInfoRequest
    ): ResponseEntity<ApiResponse<Unit?>> {
        petService.addPetInfo(petId, userId, request)
        return ResponseEntity.ok(ApiResponse.success(null))
    }

    @Operation(summary = "추모 공간 생성 상태 조회", description = "AI 이미지 생성 작업의 진행 상태를 조회합니다. " +
            "생성 중: PENDING, 생성 성공: SUCCESS, 생성 실패: FAIL")
    @GetMapping("/{petId}/status")
    fun getPetStatus(
        @AuthenticationPrincipal userId: Long,
        @PathVariable petId: Long
    ): ResponseEntity<ApiResponse<PetStatusResponse>> {
        val response = petService.getPetStatus(petId, userId)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @Operation(summary = "개인 추모 공간 목록 조회", description = "개인 추모 공간 목록을 조회합니다.")
    @GetMapping("/me")
    fun getMyMemorials(
        @AuthenticationPrincipal userId: Long
    ): ResponseEntity<ApiResponse<PetPrivateMemorialListResponse>> {
        val response = petService.getMyMemorials(userId)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @Operation(summary = "개인 추모 공간 상세 조회", description = "petId에 해당하는 개인 추모 공간의 상세 정보를 조회합니다.")
    @GetMapping("/me/{petId}")
    fun getMemorialDetail(
        @AuthenticationPrincipal userId: Long,
        @PathVariable petId: Long
    ): ResponseEntity<ApiResponse<PetMemorialDetailResponse>> {
        val response = petService.getMemorialDetail(petId, userId)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @Operation(summary = "개인 추모 공간 삭제", description = "petId에 해당하는 개인 추모 공간을 삭제합니다.")
    @DeleteMapping("/me/{petId}/delete")
    fun deleteMyMemorial(
        @AuthenticationPrincipal userId: Long,
        @PathVariable petId: Long
    ): ResponseEntity<ApiResponse<Unit?>> {
        petService.deleteMyMemorial(petId, userId)
        return ResponseEntity.ok(ApiResponse.success(null))
    }

    @Operation(summary = "쪽지 목록 조회", description = "petId에 해당하는 추모 공간에 도착한 쪽지 목록을 조회합니다.")
    @GetMapping("/me/{petId}/letters")
    fun getLetters(
        @AuthenticationPrincipal userId: Long,
        @PathVariable petId: Long
    ): ResponseEntity<ApiResponse<PetLetterListResponse>> {
        val response = petService.getLetters(petId, userId)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @Operation(summary = "공개 추모 공간 목록 조회", description = "공개된 추모 공간 목록을 조회합니다. species: 전체 - `ALL` (생략시 ALL로 들어감), " +
            "강아지: `DOG`, 고양이: `CAT`")
    @SecurityRequirements
    @GetMapping("/public")
    fun getPublicMemorials(
        @RequestParam(defaultValue = "ALL") species: String
    ): ResponseEntity<ApiResponse<PetMemorialPublicListResponse>> {
        val response = petService.getPublicMemorials(species)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @Operation(summary = "공개 추모 공간 상세 조회", description = "petId에 해당하는 공개 추모 공간의 상세 정보를 조회합니다.")
    @SecurityRequirements
    @GetMapping("/public/{petId}")
    fun getPublicMemorialDetail(
        @PathVariable petId: Long
    ): ResponseEntity<ApiResponse<PetMemorialPublicDetailResponse>> {
        val response = petService.getPublicMemorialDetail(petId)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @Operation(summary = "쪽지 작성", description = "공개된 추모 공간에 쪽지를 작성합니다. " +
            "로그인하지 않은 경우에도 작성할 수 있으며, 이 경우 무조건 익명으로 처리됩니다.")
    @SecurityRequirements
    @PostMapping("/public/{petId}/letter")
    fun createLetter(
        @AuthenticationPrincipal userId: Long?,
        @PathVariable petId: Long,
        @Valid @RequestBody request: PetLetterCreateRequest
    ): ResponseEntity<ApiResponse<PetLetterCreateResponse>> {
        val response = petService.createLetter(petId, userId, request)
        return ResponseEntity.ok(ApiResponse.success(response))
    }
}
