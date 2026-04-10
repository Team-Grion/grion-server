package com.project.grionserver.controller

import com.project.grionserver.dto.internal.TranslationRequest
import com.project.grionserver.service.TranslatorService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/internal/translation")
class TranslationController(
    private val translatorService: TranslatorService,
) {
    @PostMapping("/translate")
    fun translatePrompt(
        @RequestBody request: TranslationRequest
    ): String {
        // 다른 ai에 사용할 프롬프트를 영어로 번역 후 결과 반환
        return translatorService.translate(request.prompt)
    }
}