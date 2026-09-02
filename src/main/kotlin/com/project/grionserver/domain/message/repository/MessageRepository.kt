package com.project.grionserver.domain.message.repository

import com.project.grionserver.domain.message.entity.Message
import com.project.grionserver.domain.pet.entity.Pet
import com.project.grionserver.domain.user.entity.User
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface MessageRepository : JpaRepository<Message, Long> {

    // 특정 반려동물 메시지를 최신순으로 조회
    fun findAllByPetOrderByCreatedAtDesc(pet: Pet): List<Message>

    // 특정 반려동물에게 온 메시지 개수
    fun countByPet(pet: Pet): Long

    // 특정 반려동물의 쪽지를 일괄 소프트 삭제
    @Modifying
    @Query(
        """
        UPDATE Message m
        SET m.deletedAt = :now
        WHERE m.pet = :pet
          AND m.deletedAt IS NULL
        """
    )
    fun softDeleteByPet(pet: Pet, now: LocalDateTime): Int

    // 특정 유저가 보낸 메시지를 최신순으로 조회 (pet 연관관계 함께 조회)
    @EntityGraph(attributePaths = ["pet"])
    fun findAllBySenderOrderByCreatedAtDesc(sender: User): List<Message>

    // 특정 유저가 보낸 메시지 단건 조회
    fun findByIdAndSenderId(id: Long, senderId: Long): Message?

    // 공개된 추모 공간에 오늘 온 메시지 개수
    @Query("SELECT COUNT(m) FROM Message m WHERE m.pet.isShared = true AND m.createdAt >= :start AND m.createdAt < :end")
    fun countTodayMessagesForPublicMemorials(start: LocalDateTime, end: LocalDateTime): Long

    // 오늘 쪽지를 받은 공개 추모 공간 개수
    @Query("SELECT COUNT(DISTINCT m.pet.id) FROM Message m WHERE m.pet.isShared = true AND m.createdAt >= :start AND m.createdAt < :end")
    fun countTodayMemorialsWithMessages(start: LocalDateTime, end: LocalDateTime): Long

    // 여러 반려동물의 오늘 메시지 개수를 한 번에 집계
    @Query(
        """
        SELECT m.pet.id AS petId, COUNT(m) AS count
        FROM Message m
        WHERE m.pet IN :pets AND m.createdAt >= :start AND m.createdAt < :end
        GROUP BY m.pet.id
        """
    )
    fun countTodayMessagesGroupedByPet(pets: List<Pet>, start: LocalDateTime, end: LocalDateTime): List<PetMessageCountView>

    // 여러 반려동물의 전체 메시지 개수를 한 번에 집계
    @Query(
        """
        SELECT m.pet.id AS petId, COUNT(m) AS count
        FROM Message m
        WHERE m.pet IN :pets
        GROUP BY m.pet.id
        """
    )
    fun countMessagesGroupedByPet(pets: List<Pet>): List<PetMessageCountView>
}

interface PetMessageCountView {
    fun getPetId(): Long
    fun getCount(): Long
}
