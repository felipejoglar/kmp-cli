package com.example.kmptemplate.application.config.environment

internal actual fun createEnvironmentConfig(environment: Environment): EnvironmentConfig {
    return IosEnvironmentConfig(environment)
}

private class IosEnvironmentConfig(override val environment: Environment) : EnvironmentConfig {
    override val baseUrl: String
        get() = when (environment) {
            Environment.DEV -> "http://localhost:8080"
            Environment.BETA -> BETA_BASE_URL
            Environment.PROD -> PROD_BASE_URL
        }
}
