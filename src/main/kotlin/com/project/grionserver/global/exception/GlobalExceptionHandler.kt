package com.project.grionserver.global.exception

import com.project.grionserver.global.response.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(e: IllegalArgumentException): ResponseEntity<ApiResponse<Unit?>> {
        val statusCode = HttpStatus.BAD_REQUEST.value()
        return ResponseEntity.status(statusCode)
            .body(ApiResponse.fail(e.message ?: "잘못된 요청입니다.", statusCode))
    }

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(e: NotFoundException): ResponseEntity<ApiResponse<Unit?>> {
        val statusCode = HttpStatus.NOT_FOUND.value()
        return ResponseEntity.status(statusCode)
            .body(ApiResponse.fail(e.message ?: "리소스를 찾을 수 없습니다.", statusCode))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Unit?>> {
        val message = e.bindingResult.fieldErrors
            .joinToString(", ") { it.defaultMessage ?: "잘못된 요청입니다." }
        val statusCode = HttpStatus.BAD_REQUEST.value()
        return ResponseEntity.status(statusCode)
            .body(ApiResponse.fail(message, statusCode))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ApiResponse<Unit?>> {
        val statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value()
        return ResponseEntity.status(statusCode)
            .body(ApiResponse.fail("서버 내부 오류가 발생했습니다.", statusCode))
    }
}
