package com.project.grionserver.domain.pet.service

import com.project.grionserver.domain.image.entity.AiImageTask
import com.project.grionserver.domain.image.entity.PetImage
import com.project.grionserver.domain.image.event.AiImageGenerationRequestedEvent
import com.project.grionserver.domain.image.repository.AiImageTaskRepository
import com.project.grionserver.domain.image.repository.PetImageRepository
import com.project.grionserver.domain.message.repository.MessageRepository
import com.project.grionserver.domain.pet.dto.PetCreateRequest
import com.project.grionserver.domain.pet.dto.PetCreateResponse
import com.project.grionserver.domain.pet.dto.PetMemorialDetailResponse
import com.project.grionserver.domain.pet.dto.PetAdditionalInfoRequest
import com.project.grionserver.domain.pet.dto.PetStatusResponse
import com.project.grionserver.domain.pet.entity.Pet
import com.project.grionserver.domain.pet.entity.Species
import com.project.grionserver.domain.pet.repository.PetRepository
import com.project.grionserver.domain.user.repository.UserRepository
import com.project.grionserver.global.exception.NotFoundException
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
            .orElseThrow { NotFoundException("사용자를 찾을 수 없습니다.") }

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


    fun getMemorialDetail(petId: Long): PetMemorialDetailResponse {
        val pet = petRepository.findById(petId)
            .orElseThrow { NotFoundException("반려동물을 찾을 수 없습니다.") }

        val task = aiImageTaskRepository.findFirstByPetOrderByIdDesc(pet)
        val letterCount = messageRepository.countByPet(pet) // Todo: 추후 승인된 메시지만 계산

        return PetMemorialDetailResponse(
            petId = pet.id,
            petName = pet.name,
            aiImageUrl = task?.resultUrl,
            birthDate = pet.birthday,
            deathDate = pet.deathDate,
            letterCount = letterCount,
            content = pet.memories,
            isPublic = pet.isShared
        )
    }

    fun addPetInfo(petId: Long, request: PetAdditionalInfoRequest) {
        require(!request.deathDate.isBefore(request.birthDate)) {
            "기일은 생일보다 빠를 수 없습니다."
        }

        val pet = petRepository.findById(petId)
            .orElseThrow { NotFoundException("반려동물을 찾을 수 없습니다.") }

        pet.name = request.petName
        pet.birthday = request.birthDate
        pet.deathDate = request.deathDate
        pet.memories = request.memory
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

    fun getPetStatus(petId: Long): PetStatusResponse {
        val pet = petRepository.findById(petId)
            .orElseThrow { NotFoundException("반려동물을 찾을 수 없습니다.") }

        val task = aiImageTaskRepository.findFirstByPetOrderByIdDesc(pet)
            ?: throw IllegalArgumentException("진행 중인 작업을 찾을 수 없습니다.")

        return PetStatusResponse(status = task.status)
    }
}
