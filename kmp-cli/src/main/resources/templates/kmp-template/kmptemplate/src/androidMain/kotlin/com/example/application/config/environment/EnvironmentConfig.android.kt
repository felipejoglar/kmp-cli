package com.example.kmptemplate.application.config.environment

internal actual fun createEnvironmentConfig(environment: Environment): EnvironmentConfig {
    return AndroidEnvironmentConfig(environment)
}

private class AndroidEnvironmentConfig(override val environment: Environment) : EnvironmentConfig {
    override val baseUrl: String
        get() = when (environment) {
            Environment.DEV -> "http://10.0.2.2:8080"
            Environment.BETA -> BETA_BASE_URL
            Environment.PROD -> PROD_BASE_URL
        }
}
