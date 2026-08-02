package com.project.grionserver.domain.pet.entity

enum class Species {
    CAT,
    DOG;

    companion object {
        fun fromString(value: String): Species =
            entries.find { it.name.equals(value, ignoreCase = true) }
                ?: throw IllegalArgumentException("알 수 없는 반려동물 종입니다: $value")
    }
}
