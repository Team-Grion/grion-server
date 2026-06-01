package com.project.grionserver.domain.image.repository

import com.project.grionserver.domain.image.entity.PetImage
import com.project.grionserver.domain.pet.entity.Pet
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PetImageRepository : JpaRepository<PetImage, Long> {
    // 특정 반려동물 이미지들 조회
    fun findAllByPet(pet: Pet): List<PetImage>
}
