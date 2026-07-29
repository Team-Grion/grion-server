package com.project.grionserver.domain.image.service

import com.project.grionserver.domain.flux.service.FluxService
import com.project.grionserver.domain.image.entity.PetImage
import com.project.grionserver.domain.image.event.AiImageGenerationRequestedEvent
import com.project.grionserver.domain.image.repository.AiImageTaskRepository
import com.project.grionserver.domain.image.repository.PetImageRepository
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Service
class AiImageGenerationService(
    private val fluxService: FluxService,
    private val aiImageTaskRepository: AiImageTaskRepository,
    private val petImageRepository: PetImageRepository
) {
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handle(event: AiImageGenerationRequestedEvent) {
        val task = aiImageTaskRepository.findById(event.taskId).orElse(null) ?: return

        try {
            val editedUrl = fluxService.editImage(event.prompt, event.sourceImageUrl)
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
}
