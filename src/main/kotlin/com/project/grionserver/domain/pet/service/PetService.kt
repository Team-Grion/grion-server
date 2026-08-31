package com.project.grionserver.domain.pet.service

import com.project.grionserver.domain.image.entity.AiImageTask
import com.project.grionserver.domain.image.entity.PetImage
import com.project.grionserver.domain.image.event.AiImageGenerationRequestedEvent
import com.project.grionserver.domain.image.repository.AiImageTaskRepository
import com.project.grionserver.domain.image.repository.PetImageRepository
import com.project.grionserver.domain.message.entity.Message
import com.project.grionserver.domain.message.repository.MessageRepository
import com.project.grionserver.domain.moderation.ModerationService
import com.project.grionserver.domain.pet.dto.PetCreateRequest
import com.project.grionserver.domain.pet.dto.PetCreateResponse
import com.project.grionserver.domain.pet.dto.PetLetterListResponse
import com.project.grionserver.domain.pet.dto.PetLetterSummary
import com.project.grionserver.domain.pet.dto.PetLetterCreateRequest
import com.project.grionserver.domain.pet.dto.PetLetterCreateResponse
import com.project.grionserver.domain.pet.dto.PetPrivateMemorialListResponse
import com.project.grionserver.domain.pet.dto.PetPrivateMemorialSummary
import com.project.grionserver.domain.pet.dto.PetMemorialUpdateResponse
import com.project.grionserver.domain.pet.dto.PetMemorialUpdateRequest
import com.project.grionserver.domain.pet.dto.PetMemorialDetailResponse
import com.project.grionserver.domain.pet.dto.PetAdditionalInfoRequest
import com.project.grionserver.domain.pet.dto.PetInfoUpdateRequest
import com.project.grionserver.domain.pet.dto.PetInfoUpdateResponse
import com.project.grionserver.domain.pet.dto.PetMemorialPublicDetailResponse
import com.project.grionserver.domain.pet.dto.PetMemorialPublicListResponse
import com.project.grionserver.domain.pet.dto.PetMemorialPublicSummary
import com.project.grionserver.domain.pet.dto.PetMemorialPublicTodaySummary
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
import java.time.LocalDate
import java.time.LocalDateTime
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
    private val eventPublisher: ApplicationEventPublisher,
    private val moderationService: ModerationService
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
                species = species,
                breed = request.breed,
                personalities = request.personalities,
                background = request.background
            )
        )

        return PetCreateResponse(petId = pet.id, status = task.status)
    }

    fun updateMemorial(petId: Long, userId: Long, request: PetMemorialUpdateRequest): PetMemorialUpdateResponse {
        val pet = petRepository.findByIdAndUserId(petId, userId)
            ?: throw NotFoundException("반려동물을 찾을 수 없습니다.")

        request.content?.let { pet.memories = it }
        request.isPublic?.let { pet.isShared = it }
        petRepository.saveAndFlush(pet)

        return toMemorialDetailResponse(pet)
    }

    fun updatePetInfo(
        petId: Long,
        userId: Long,
        petImage: MultipartFile?,
        request: PetInfoUpdateRequest
    ): PetInfoUpdateResponse {
        val pet = petRepository.findByIdAndUserId(petId, userId)
            ?: throw NotFoundException("반려동물을 찾을 수 없습니다.")

        request.breed?.let { pet.breed = it }
        request.personalities?.let { pet.personalities = it.toMutableList() }
        request.background?.let { pet.backgroundText = it }

        val shouldRegenerateImage = petImage != null ||
                request.breed != null ||
                request.personalities != null ||
                request.background != null

        var imageStatus: String? = null

        if (shouldRegenerateImage) {
            val sourceImageUrl = if (petImage != null) {
                val uploadedUrl = falStorageService.upload(petImage)
                petImageRepository.save(PetImage(pet = pet, imageUrl = uploadedUrl, isMain = true))
                uploadedUrl
            } else {
                petImageRepository.findFirstByPetAndIsMainTrueOrderByIdDesc(pet)?.imageUrl
                    ?: throw NotFoundException("원본 사진을 찾을 수 없습니다.")
            }

            val task = aiImageTaskRepository.save(
                AiImageTask(pet = pet, requestId = UUID.randomUUID().toString(), status = "PENDING")
            )

            eventPublisher.publishEvent(
                AiImageGenerationRequestedEvent(
                    taskId = task.id,
                    sourceImageUrl = sourceImageUrl,
                    species = pet.species,
                    breed = pet.breed,
                    personalities = pet.personalities.toList(),
                    background = pet.backgroundText ?: ""
                )
            )

            imageStatus = task.status
        }

        petRepository.saveAndFlush(pet)

        return PetInfoUpdateResponse(
            petId = pet.id,
            species = pet.species.name,
            breed = pet.breed,
            personalities = pet.personalities.toList(),
            background = pet.backgroundText,
            imageStatus = imageStatus
        )
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

    fun getMemorialDetail(petId: Long, userId: Long): PetMemorialDetailResponse {
        val pet = petRepository.findByIdAndUserId(petId, userId)
            ?: throw NotFoundException("반려동물을 찾을 수 없습니다.")

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

    fun addPetInfo(petId: Long, userId: Long, request: PetAdditionalInfoRequest) {
        require(!request.deathDate.isBefore(request.birthDate)) {
            "기일은 생일보다 빠를 수 없습니다."
        }

        val pet = petRepository.findByIdAndUserId(petId, userId)
            ?: throw NotFoundException("반려동물을 찾을 수 없습니다.")

        pet.name = request.petName
        pet.birthday = request.birthDate
        pet.deathDate = request.deathDate
        pet.memories = request.memory
    }

    fun getPetStatus(petId: Long, userId: Long): PetStatusResponse {
        val pet = petRepository.findByIdAndUserId(petId, userId)
            ?: throw NotFoundException("반려동물을 찾을 수 없습니다.")

        val task = aiImageTaskRepository.findFirstByPetOrderByIdDesc(pet)
            ?: throw IllegalArgumentException("진행 중인 작업을 찾을 수 없습니다.")

        return PetStatusResponse(status = task.status)
    }

    fun getPublicMemorials(species: String): PetMemorialPublicListResponse {
        val pets = if (species.equals("ALL", ignoreCase = true)) {
            petRepository.findAllByIsSharedTrue()
        } else {
            petRepository.findAllByIsSharedTrueAndSpecies(Species.fromString(species))
        }

        val startOfToday = LocalDate.now().atStartOfDay()
        val startOfTomorrow = startOfToday.plusDays(1)

        val latestImageTaskByPetId = if (pets.isEmpty()) {
            emptyMap()
        } else {
            aiImageTaskRepository.findLatestByPetIn(pets).associateBy { it.pet.id }
        }
        val todayMessageCountByPetId = if (pets.isEmpty()) {
            emptyMap()
        } else {
            messageRepository.countTodayMessagesGroupedByPet(pets, startOfToday, startOfTomorrow)
                .associate { it.getPetId() to it.getCount() }
        }
        val totalMessageCountByPetId = if (pets.isEmpty()) {
            emptyMap()
        } else {
            messageRepository.countMessagesGroupedByPet(pets).associate { it.getPetId() to it.getCount() }
        }

        val content = pets.map { pet ->
            PetMemorialPublicSummary(
                petId = pet.id,
                petName = pet.name,
                aiImageUrl = latestImageTaskByPetId[pet.id]?.resultUrl,
                birthDate = pet.birthday,
                deathDate = pet.deathDate,
                introduction = pet.memories,
                personalities = pet.personalities.toList(),
                todayMessageCount = todayMessageCountByPetId[pet.id] ?: 0L,
                totalMessageCount = totalMessageCountByPetId[pet.id] ?: 0L
            )
        }

        val todaySummary = PetMemorialPublicTodaySummary(
            memorialCount = petRepository.countTodayPublicMemorials(startOfToday, startOfTomorrow),
            messageCount = messageRepository.countTodayMessagesForPublicMemorials(startOfToday, startOfTomorrow)
        )

        return PetMemorialPublicListResponse(todaySummary = todaySummary, content = content)
    }

    fun getPublicMemorialDetail(petId: Long): PetMemorialPublicDetailResponse {
        val pet = petRepository.findByIdAndIsSharedTrue(petId)
            ?: throw NotFoundException("반려동물을 찾을 수 없습니다.")

        val task = aiImageTaskRepository.findFirstByPetOrderByIdDesc(pet)

        return PetMemorialPublicDetailResponse(
            petId = pet.id,
            petName = pet.name,
            userName = pet.user.nickname,
            aiImageUrl = task?.resultUrl,
            birthDate = pet.birthday,
            deathDate = pet.deathDate,
            personalities = pet.personalities.toList()
        )
    }

    fun getLetters(petId: Long, userId: Long): PetLetterListResponse {
        val pet = petRepository.findByIdAndUserId(petId, userId)
            ?: throw NotFoundException("반려동물을 찾을 수 없습니다.")

        val letters = messageRepository.findAllByPetOrderByCreatedAtDesc(pet).map { message ->
            PetLetterSummary(
                letterId = message.id,
                senderName = if (message.isAnonymous) "익명" else message.sender.nickname,
                content = message.content,
                createdAt = message.createdAt
            )
        }

        return PetLetterListResponse(petId = pet.id, letters = letters)
    }

    fun createLetter(petId: Long, userId: Long, request: PetLetterCreateRequest): PetLetterCreateResponse {
        val pet = petRepository.findByIdAndIsSharedTrueForUpdate(petId)
            ?: throw NotFoundException("반려동물을 찾을 수 없습니다.")

        val sender = userRepository.findById(userId)
            .orElseThrow { NotFoundException("사용자를 찾을 수 없습니다.") }

        if (moderationService.isViolated(request.content)) {
            throw IllegalArgumentException("부적절한 내용이 감지되어 전송되지 않았어요")
        }

        val message = messageRepository.save(
            Message(
                pet = pet,
                sender = sender,
                content = request.content,
                isAnonymous = request.isAnonymous,
                status = "APPROVED"
            )
        )

        return PetLetterCreateResponse(letterId = message.id, createdAt = message.createdAt)
    }

    fun deleteMyMemorial(petId: Long, userId: Long) {
        val pet = petRepository.findByIdAndUserIdForUpdate(petId, userId)
            ?: throw NotFoundException("반려동물을 찾을 수 없습니다.")

        val now = LocalDateTime.now()
        messageRepository.softDeleteByPet(pet, now)
        pet.deletedAt = now
    }

    fun getMyMemorials(userId: Long): PetPrivateMemorialListResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { NotFoundException("사용자를 찾을 수 없습니다.") }

        val memorials = petRepository.findAllByUser(user).map { pet ->
            val aiImageUrl = petImageRepository.findAllByPet(pet)
                .firstOrNull { !it.isMain }
                ?.imageUrl

            PetPrivateMemorialSummary(
                petId = pet.id,
                petName = pet.name,
                aiImageUrl = aiImageUrl
            )
        }

        return PetPrivateMemorialListResponse(memorials = memorials)
    }
}
