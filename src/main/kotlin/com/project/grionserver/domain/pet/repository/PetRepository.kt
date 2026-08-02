package com.project.grionserver.domain.pet.repository

import com.project.grionserver.domain.pet.entity.Pet
import com.project.grionserver.domain.pet.entity.Species
import com.project.grionserver.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository
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

    // 오늘 새로 등록된 공개 추모 공간 개수
    fun countByIsSharedTrueAndCreatedAtBetween(start: LocalDateTime, end: LocalDateTime): Long
}
