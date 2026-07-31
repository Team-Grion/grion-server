package com.project.grionserver.domain.pet.service

import com.project.grionserver.domain.image.entity.AiImageTask
import com.project.grionserver.domain.image.entity.PetImage
import com.project.grionserver.domain.image.event.AiImageGenerationRequestedEvent
import com.project.grionserver.domain.image.repository.AiImageTaskRepository
import com.project.grionserver.domain.image.repository.PetImageRepository
import com.project.grionserver.domain.message.repository.MessageRepository
import com.project.grionserver.domain.pet.dto.PetCreateRequest
import com.project.grionserver.domain.pet.dto.PetCreateResponse
import com.project.grionserver.domain.pet.dto.PetMemorialUpdateResponse
import com.project.grionserver.domain.pet.dto.PetMemorialUpdateRequest
import com.project.grionserver.domain.pet.entity.Pet
import com.project.grionserver.domain.pet.entity.Species
import com.project.grionserver.domain.pet.repository.PetRepository
import com.project.grionserver.domain.user.repository.UserRepository
import com.project.grionserver.global.service.FalStorageService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@Service
@Transactional
class PetService(
    private val petRepository: PetRepository,
    private val petImageRepository: PetImageRepository,
    private val userRepository: UserRepository,
    private val falStorageService: FalStorageService,
    private val aiImageTaskRepository: AiImageTaskRepository,
    private val messageRepository: MessageRepository,
    private val eventPublisher: ApplicationEventPublisher
) {
    fun createPet(image: MultipartFile, request: PetCreateRequest): PetCreateResponse {
        val user = userRepository.findById(request.userId)
            .orElseThrow { IllegalArgumentException("사용자를 찾을 수 없습니다.") }

        val imageUrl = falStorageService.upload(image)
        val species = Species.fromString(request.species)

        val pet = petRepository.save(
            Pet(
                user = user,
                species = species,
                breed = request.breed,
                personalities = request.personalities.toMutableList(),
                backgroundText = request.background
            )
        )

        petImageRepository.save(
            PetImage(pet = pet, imageUrl = imageUrl, isMain = true)
        )

        val task = aiImageTaskRepository.save(
            AiImageTask(
                pet = pet,
                requestId = UUID.randomUUID().toString(),
                status = "PENDING"
            )
        )

        eventPublisher.publishEvent(
            AiImageGenerationRequestedEvent(
                taskId = task.id,
                sourceImageUrl = imageUrl,
                prompt = buildPrompt(species, request)
            )
        )

        return PetCreateResponse(petId = pet.id, status = task.status)
    }

    fun updateMemorial(petId: Long, request: PetMemorialUpdateRequest): PetMemorialUpdateResponse {
        val pet = petRepository.findById(petId)
            .orElseThrow { IllegalArgumentException("반려동물을 찾을 수 없습니다.") }

        request.content?.let { pet.memories = it }
        request.isPublic?.let { pet.isShared = it }
        petRepository.saveAndFlush(pet)

        return toMemorialDetailResponse(pet)
    }

    private fun toMemorialDetailResponse(pet: Pet): PetMemorialUpdateResponse {
        val task = aiImageTaskRepository.findFirstByPetOrderByIdDesc(pet)
        val letterCount = messageRepository.countByPet(pet) // Todo: 추후 승인된 메시지만 카운트

        return PetMemorialUpdateResponse(
            petId = pet.id,
            petName = pet.name,
            aiImageUrl = task?.resultUrl,
            birthDate = pet.birthday,
            deathDate = pet.deathDate,
            letterCount = letterCount,
            content = pet.memories,
            isPublic = pet.isShared,
            updatedAt = pet.updatedAt
        )
    }

    private fun buildPrompt(species: Species, request: PetCreateRequest): String {
        val speciesText = when (species) {
            Species.CAT -> "고양이"
            Species.DOG -> "강아지"
        }
        val personalityText = request.personalities.joinToString(", ")
        return "품종이 ${request.breed}인 ${speciesText}이미지를 생성해줘. " +
            "성격은 ${personalityText}. 배경은 ${request.background}."
    }
}
