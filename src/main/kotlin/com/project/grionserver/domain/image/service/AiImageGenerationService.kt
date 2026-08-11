package com.project.grionserver.domain.image.service

import com.project.grionserver.domain.flux.service.FluxService
import com.project.grionserver.domain.image.entity.PetImage
import com.project.grionserver.domain.image.event.AiImageGenerationRequestedEvent
import com.project.grionserver.domain.image.repository.AiImageTaskRepository
import com.project.grionserver.domain.image.repository.PetImageRepository
import com.project.grionserver.domain.pet.entity.Species
import com.project.grionserver.domain.translate.service.TranslatorService
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Service
class AiImageGenerationService(
    private val fluxService: FluxService,
    private val translatorService: TranslatorService,
    private val aiImageTaskRepository: AiImageTaskRepository,
    private val petImageRepository: PetImageRepository
) {
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handle(event: AiImageGenerationRequestedEvent) {
        val task = aiImageTaskRepository.findById(event.taskId).orElse(null) ?: return

        try {
            val builtPrompt = buildPrompt(event)
            val translatedPrompt = translatorService.translate(builtPrompt)
            val prompt = if (translatedPrompt.startsWith("번역 오류 발생")) builtPrompt else translatedPrompt

            val editedUrl = fluxService.editImage(prompt, event.sourceImageUrl)
                ?: throw RuntimeException("이미지 편집 결과를 받지 못했습니다.")

            task.status = "SUCCESS"
            task.resultUrl = editedUrl

            petImageRepository.save(
                PetImage(pet = task.pet, imageUrl = editedUrl, isMain = false)
            )
        } catch (e: Exception) {
            task.status = "FAIL"
            task.failureReason = e.message
        }
    }

    private fun buildPrompt(event: AiImageGenerationRequestedEvent): String {
        val speciesText = when (event.species) {
            Species.CAT -> "고양이"
            Species.DOG -> "강아지"
        }
        val personalityText = event.personalities.joinToString(", ")
        val ambiguousBreeds = setOf("믹스견", "믹스묘", "기타")
        val breedText = if (event.breed in ambiguousBreeds) {
            ""
        } else {
            " 품종은 ${event.breed}이며, "
        }

        return "이 ${speciesText}의 품종 특징(털색, 무늬, 귀·얼굴 형태 등)은 사진을 최우선으로 유지해줘." +
                breedText +
                " 자세와 구도, 표정은 성격과 배경에 어울리게 표현해줘." +
                " 성격은 ${personalityText}. 배경은 ${event.background}."
    }
}
