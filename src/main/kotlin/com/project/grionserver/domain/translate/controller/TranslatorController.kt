package com.project.grionserver.domain.translate.controller

import com.project.grionserver.domain.translate.dto.internal.TranslationRequest
import com.project.grionserver.domain.translate.service.TranslatorService
import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@Hidden
@Tag(name = "Translation", description = "번역 API")
@RestController
@RequestMapping("/api/internal/translation")
class TranslationController(
    private val translatorService: TranslatorService,
) {
    @Operation(summary = "프롬프트 번역", description = "AI 프롬프트를 영어로 번역합니다.")
    @PostMapping("/translate")
    fun translatePrompt(
        @RequestBody request: TranslationRequest
    ): String {
        // 다른 ai에 사용할 프롬프트를 영어로 번역 후 결과 반환
        return translatorService.translate(request.prompt)
    }
}
