package com.project.grionserver.global.exception

import com.project.grionserver.global.response.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingRequestHeaderException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.multipart.support.MissingServletRequestPartException

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

    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorizedException(e: UnauthorizedException): ResponseEntity<ApiResponse<Unit?>> {
        val statusCode = HttpStatus.UNAUTHORIZED.value()
        return ResponseEntity.status(statusCode)
            .body(ApiResponse.fail(e.message ?: "인증에 실패했습니다.", statusCode))
    }

    @ExceptionHandler(BadGatewayException::class)
    fun handleBadGatewayException(e: BadGatewayException): ResponseEntity<ApiResponse<Unit?>> {
        val statusCode = HttpStatus.BAD_GATEWAY.value()
        return ResponseEntity.status(statusCode)
            .body(ApiResponse.fail(e.message ?: "외부 서비스와 통신할 수 없습니다.", statusCode))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Unit?>> {
        val message = e.bindingResult.fieldErrors
            .joinToString(", ") { it.defaultMessage ?: "잘못된 요청입니다." }
        val statusCode = HttpStatus.BAD_REQUEST.value()
        return ResponseEntity.status(statusCode)
            .body(ApiResponse.fail(message, statusCode))
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(e: HttpMessageNotReadableException): ResponseEntity<ApiResponse<Unit?>> {
        val statusCode = HttpStatus.BAD_REQUEST.value()
        return ResponseEntity.status(statusCode)
            .body(ApiResponse.fail("요청 본문을 읽을 수 없습니다.", statusCode))
    }

    @ExceptionHandler(MissingRequestHeaderException::class)
    fun handleMissingRequestHeaderException(e: MissingRequestHeaderException): ResponseEntity<ApiResponse<Unit?>> {
        val statusCode = HttpStatus.BAD_REQUEST.value()
        return ResponseEntity.status(statusCode)
            .body(ApiResponse.fail("필수 헤더가 누락되었습니다: ${e.headerName}", statusCode))
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleMethodArgumentTypeMismatchException(e: MethodArgumentTypeMismatchException): ResponseEntity<ApiResponse<Unit?>> {
        val statusCode = HttpStatus.BAD_REQUEST.value()
        return ResponseEntity.status(statusCode)
            .body(ApiResponse.fail("요청 파라미터 형식이 올바르지 않습니다: ${e.name}", statusCode))
    }

    @ExceptionHandler(MissingServletRequestPartException::class)
    fun handleMissingServletRequestPartException(e: MissingServletRequestPartException): ResponseEntity<ApiResponse<Unit?>> {
        val statusCode = HttpStatus.BAD_REQUEST.value()
        return ResponseEntity.status(statusCode)
            .body(ApiResponse.fail("필수 요청 파트가 누락되었습니다: ${e.requestPartName}", statusCode))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ApiResponse<Unit?>> {
        val statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value()
        return ResponseEntity.status(statusCode)
            .body(ApiResponse.fail("서버 내부 오류가 발생했습니다.", statusCode))
    }
}
