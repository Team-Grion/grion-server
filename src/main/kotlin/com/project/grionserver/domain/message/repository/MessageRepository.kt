package com.project.grionserver.domain.message.repository

import com.project.grionserver.domain.message.entity.Message
import com.project.grionserver.domain.pet.entity.Pet
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MessageRepository : JpaRepository<Message, Long> {
    // 특정 반려동물 메시지 조회
    fun findAllByPet(pet: Pet): List<Message>
}
