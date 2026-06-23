package com.project.grionserver.domain.pet.controller

import com.project.grionserver.domain.pet.dto.PetCreateRequest
import com.project.grionserver.domain.pet.dto.PetCreateResponse
import com.project.grionserver.domain.pet.service.PetService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate

@RestController
@RequestMapping("/pet")
class PetController(private val petService: PetService) {

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createPet(
        @RequestParam userId: Long,
        @RequestParam name: String,
        @RequestPart image: MultipartFile,
        @RequestParam species: String,
        @RequestParam breed: String,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) birthday: LocalDate?,
        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) deathDate: LocalDate?,
        @RequestParam characteristics: String?,
        @RequestParam(required = false) memories: String?,
        @RequestParam backgroundText: String?
    ): ResponseEntity<PetCreateResponse> {
        val request = PetCreateRequest(
            userId = userId,
            name = name,
            species = species,
            breed = breed,
            birthday = birthday,
            deathDate = deathDate,
            characteristics = characteristics,
            memories = memories,
            backgroundText = backgroundText
        )
        return ResponseEntity.ok(petService.createPet(image, request))
    }
}
