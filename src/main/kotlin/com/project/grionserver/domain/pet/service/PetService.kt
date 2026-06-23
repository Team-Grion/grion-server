package com.project.grionserver.domain.pet.service

import com.project.grionserver.domain.image.entity.PetImage
import com.project.grionserver.domain.image.repository.PetImageRepository
import com.project.grionserver.domain.pet.dto.PetCreateRequest
import com.project.grionserver.domain.pet.dto.PetCreateResponse
import com.project.grionserver.domain.pet.entity.Pet
import com.project.grionserver.domain.pet.repository.PetRepository
import com.project.grionserver.domain.user.repository.UserRepository
import com.project.grionserver.global.service.FalStorageService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
@Transactional
class PetService(
    private val petRepository: PetRepository,
    private val petImageRepository: PetImageRepository,
    private val userRepository: UserRepository,
    private val falStorageService: FalStorageService
) {
    fun createPet(image: MultipartFile, request: PetCreateRequest): PetCreateResponse {
        val user = userRepository.findById(request.userId)
            .orElseThrow { IllegalArgumentException("사용자를 찾을 수 없습니다.") }

        val imageUrl = falStorageService.upload(image)

        val pet = petRepository.save(
            Pet(
                user = user,
                name = request.name,
                birthday = request.birthday,
                deathDate = request.deathDate,
                species = request.species,
                breed = request.breed,
                characteristics = request.characteristics,
                backgroundText = request.backgroundText,
                memories = request.memories
            )
        )

        petImageRepository.save(
            PetImage(
                pet = pet,
                imageUrl = imageUrl,
                isMain = true
            )
        )

        return PetCreateResponse(
            id = pet.id,
            name = pet.name,
            species = pet.species,
            breed = pet.breed,
            birthday = pet.birthday,
            deathDate = pet.deathDate,
            imageUrl = imageUrl
        )
    }
}
