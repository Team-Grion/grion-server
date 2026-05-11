package com.project.grionserver.service

import com.deepl.api.DeepLClient
import com.deepl.api.Translator
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class TranslatorService(
    @Value($$"${deepl.api.key}") private val authKey: String
) {
    private lateinit var translator: Translator

    @PostConstruct
    fun init() {
        translator = DeepLClient(authKey)
    }

    fun translate(text: String): String {
        if (text.isBlank()) return ""

        return try {
            val result = translator.translateText(text, null, "en-US")
            result.text
        } catch (e: Exception) {
            "번역 오류 발생: ${e.message}"
        }
    }
}
