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

        return "너는 15년 경력의 반려동물 전문 포토그래퍼야. 행복했던 순간을 자연스럽게 담아내는 게 특기고, 지금 이 ${speciesText}를 직접 촬영했다고 생각하고 편집해줘." +
                " 사진 속 대상의 생김새(털색, 무늬, 눈동자 색, 귀와 얼굴 형태, 체형 등)와 이 개체만의 고유한 비대칭적 특징을 최대한 그대로 유지해줘." +
                " 전형적인 품종 표준 이미지처럼 더 예쁘게 다듬거나 이상화하지 마." +
                " 자세와 구도, 표정은 성격과 배경에 어울리게 자연스럽게 표현해줘. 실제 카메라로 찍은 듯한 자연스러운 스냅샷처럼 만들어줘." +
                " 성격은 ${personalityText}. 배경은 ${event.background}."
    }
}
