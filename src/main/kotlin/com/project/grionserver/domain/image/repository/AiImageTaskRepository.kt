package com.project.grionserver.domain.image.repository

import com.project.grionserver.domain.image.entity.AiImageTask
import com.project.grionserver.domain.pet.entity.Pet
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface AiImageTaskRepository : JpaRepository<AiImageTask, Long> {
    // 특정 반려동물의 AI 작업 상태 확인
    fun findFirstByPetOrderByIdDesc(pet: Pet): AiImageTask?

    // 여러 반려동물의 가장 최근 AI 작업을 한 번에 조회
    @Query(
        """
        SELECT t FROM AiImageTask t
        WHERE t.id IN (
            SELECT MAX(t2.id) FROM AiImageTask t2 WHERE t2.pet IN :pets GROUP BY t2.pet
        )
        """
    )
    fun findLatestByPetIn(pets: List<Pet>): List<AiImageTask>
}
