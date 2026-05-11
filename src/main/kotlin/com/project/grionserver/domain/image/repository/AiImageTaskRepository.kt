package com.project.grionserver.domain.image.repository

import com.project.grionserver.domain.image.entity.AiImageTask
import com.project.grionserver.domain.pet.entity.Pet
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AiImageTaskRepository : JpaRepository<AiImageTask, Long> {
    // 특정 반려동물의 AI 작업 상태 확인
    fun findByPet(pet: Pet): AiImageTask?

    // 아직 처리되지 않은 작업들 조회
    fun findAllByStatus(status: String): List<AiImageTask>
}
