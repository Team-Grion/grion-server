package com.project.grionserver.global.config

import jakarta.annotation.PostConstruct
import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URI

@ConfigurationProperties(prefix = "cors")
data class CorsProperties(
    val allowedOrigins: List<String>
) {

    @PostConstruct
    fun validate() {
        require(allowedOrigins.isNotEmpty()) { "cors.allowed-origins must not be empty" }
        allowedOrigins.forEach { origin ->
            require(isTrustedLocalOrigin(origin) || isValidHttpsOrigin(origin)) {
                "Invalid cors.allowed-origins entry: '$origin'. " +
                    "Must be an exact https origin with no path/query/fragment (e.g. https://grion.com)."
            }
        }
    }

    private fun isTrustedLocalOrigin(origin: String) = origin == LOCAL_DEV_ORIGIN

    private fun isValidHttpsOrigin(origin: String): Boolean {
        val uri = runCatching { URI(origin) }.getOrNull() ?: return false
        return uri.scheme == "https" &&
            !uri.host.isNullOrBlank() &&
            uri.path.isNullOrEmpty() &&
            uri.query == null &&
            uri.fragment == null &&
            uri.userInfo == null
    }

    companion object {
        private const val LOCAL_DEV_ORIGIN = "http://localhost:3000"
    }
}
