package com.example.kmptemplate.application.config.environment

/**
 * Contract for accessing environment-specific configuration information.
 */
internal interface EnvironmentConfig {
    /**
     * The base URL for API requests in this environment.
     */
    val baseUrl: String

    /**
     * The current environment (DEV, BETA, or PROD).
     */
    val environment: Environment
}

internal expect fun createEnvironmentConfig(environment: Environment): EnvironmentConfig


internal const val BETA_BASE_URL = ""
internal const val PROD_BASE_URL = ""