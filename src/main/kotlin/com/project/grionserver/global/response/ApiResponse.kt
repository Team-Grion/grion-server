package com.project.grionserver.global.response

data class ApiResponse<T>(
    val status: String,
    val statusCode: Int,
    val data: T
) {
    companion object {
        fun <T> success(data: T, statusCode: Int = 200): ApiResponse<T> =
            ApiResponse(status = "SUCCESS", statusCode = statusCode, data = data)
    }
}
