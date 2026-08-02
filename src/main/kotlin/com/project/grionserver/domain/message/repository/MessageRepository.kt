package com.project.grionserver.domain.message.repository

import com.project.grionserver.domain.message.entity.Message
import com.project.grionserver.domain.pet.entity.Pet
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface MessageRepository : JpaRepository<Message, Long> {
    // 특정 반려동물 메시지 조회
    fun findAllByPet(pet: Pet): List<Message>

    // 특정 반려동물에게 온 메시지 개수
    fun countByPet(pet: Pet): Long

    // 특정 반려동물에게 오늘 온 메시지 개수
    fun countByPetAndCreatedAtBetween(pet: Pet, start: LocalDateTime, end: LocalDateTime): Long

    // 공개된 추모 공간에 오늘 온 메시지 개수
    @Query("SELECT COUNT(m) FROM Message m WHERE m.pet.isShared = true AND m.createdAt BETWEEN :start AND :end")
    fun countTodayMessagesForPublicMemorials(start: LocalDateTime, end: LocalDateTime): Long
}
