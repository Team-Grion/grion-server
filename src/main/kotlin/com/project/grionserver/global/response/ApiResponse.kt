package com.project.grionserver.global.response

data class ApiResponse<T>(
    val status: String,
    val statusCode: Int,
    val data: T,
    val message: String? = null
) {
    companion object {
        fun <T> success(data: T, statusCode: Int = 200): ApiResponse<T> =
            ApiResponse(status = "SUCCESS", statusCode = statusCode, data = data)

        fun fail(message: String, statusCode: Int): ApiResponse<Unit?> =
            ApiResponse(status = "FAIL", statusCode = statusCode, data = null, message = message)
    }
}
