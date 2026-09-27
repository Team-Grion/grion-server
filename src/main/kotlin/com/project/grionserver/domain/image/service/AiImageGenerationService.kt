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
        val expressionText = when (event.species) {
            Species.CAT -> " 표정과 입 모양은 실제 고양이의 얼굴 구조와 습성에 맞게, 고양이다운 모습으로 표현해줘."
            Species.DOG -> ""
        }

        return "너는 15년 경력의 반려동물 전문 포토그래퍼야. 행복했던 순간을 자연스럽게 담아내는 게 특기고," +
                " 사진 속 이 ${speciesText}를 ${event.background}에서 새로 촬영한 사진을 만들어줘. 이 아이의 성격: ${personalityText}." +
                " [반드시 유지할 것] 사진 속 아이와 한눈에 같은 개체로 알아볼 수 있어야 해." +
                " 털 색과 무늬의 위치·모양·크기, 얼굴 무늬, 눈동자 색과 눈 모양, 코 색, 귀 모양, 털 길이와 질감, 체형과 크기를 사진과 똑같이 재현하고," +
                " 이 개체만의 비대칭적인 특징까지, 사진 속 실제 모습을 있는 그대로 담아줘." +
                " [자유롭게 연출할 것] 자세, 구도, 촬영 각도는 배경과 성격을 기준으로 자유롭게 정해줘." +
                " 원본 자세가 그 장면에 잘 어울리면 비슷하게 유지해도 되고, 이 아이가 그 장소에서 실제로 하고 있을 법한 다른 자세와 행동으로 바꿔도 좋아." +
                " 어느 쪽이든 이 아이가 실제로 그 장소에 있는 것처럼 자연스럽게 어우러지게 해줘." +
                " 표정도 상황과 성격에 어울리게 자연스럽게 표현해줘.${expressionText}" +
                " 빛과 그림자, 색감은 배경과 자연스럽게 어우러지게 하고, 실제 카메라로 찍은 듯한 자연스러운 스냅샷처럼 만들어줘."
    }
}
