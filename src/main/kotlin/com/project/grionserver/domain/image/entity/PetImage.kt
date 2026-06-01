package com.project.grionserver.domain.image.entity

import com.project.grionserver.domain.common.entity.BaseCreatedAtEntity
import com.project.grionserver.domain.pet.entity.Pet
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.Comment

@Entity
@Table(name = "pet_image")
class PetImage(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @Comment("이미지 PK")
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false, foreignKey = ForeignKey(name = "FK_PET_IMAGE_PET"))
    val pet: Pet,

    @Column(name = "image_url", nullable = false, columnDefinition = "TEXT")
    @Comment("이미지 경로")
    val imageUrl: String,

    @Column(name = "is_main", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    @Comment("대표 사진 여부")
    var isMain: Boolean = false
) : BaseCreatedAtEntity()
