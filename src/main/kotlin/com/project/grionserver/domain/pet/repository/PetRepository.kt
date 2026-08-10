package com.project.grionserver.domain.pet.repository

import com.project.grionserver.domain.pet.entity.Pet
import com.project.grionserver.domain.pet.entity.Species
import com.project.grionserver.domain.user.entity.User
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface PetRepository : JpaRepository<Pet, Long> {
    // 특정 유저가 등록한 반려동물 목록 조회
    fun findAllByUser(user: User): List<Pet>

    // 공개된 추모 페이지 목록 조회
    fun findAllByIsSharedTrue(): List<Pet>

    // 공개된 추모 페이지 목록 조회
    fun findAllByIsSharedTrueAndSpecies(species: Species): List<Pet>

    // 공개된 추모 페이지 단건 조회
    fun findByIdAndIsSharedTrue(id: Long): Pet?

    // 소유자 검증을 포함한 단건 조회
    fun findByIdAndUserId(id: Long, userId: Long): Pet?

    // 소유자 검증 + 락 조회 (추모 공간 삭제용)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Pet p WHERE p.id = :id AND p.user.id = :userId")
    fun findByIdAndUserIdForUpdate(id: Long, userId: Long): Pet?

    // 공개 여부 검증 + 락 조회 (쪽지 작성용)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Pet p WHERE p.id = :id AND p.isShared = true")
    fun findByIdAndIsSharedTrueForUpdate(id: Long): Pet?

    // 오늘 새로 등록된 공개 추모 공간 개수
    @Query("SELECT COUNT(p) FROM Pet p WHERE p.isShared = true AND p.createdAt >= :start AND p.createdAt < :end")
    fun countTodayPublicMemorials(start: LocalDateTime, end: LocalDateTime): Long
}
