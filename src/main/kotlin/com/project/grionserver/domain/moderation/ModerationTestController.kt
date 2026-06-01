package com.project.grionserver.domain.moderation

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ModerationTestController(
    private val moderationService: ModerationService
) {

    @GetMapping("/api/v1/test/moderation")
    fun testModeration(@RequestParam("text") text: String): String {
        val isViolated = moderationService.isViolated(text)

        return if (isViolated) {
            "검열 통과 실패, 부적절합니다."
        } else {
            "검열 통과 성공, 적절합니다."
        }
    }
}
