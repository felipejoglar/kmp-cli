package com.example.kmptemplate.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.kmptemplate.App
import com.example.kmptemplate.android.application.config.device.AndroidDeviceConfig
import com.example.kmptemplate.application.config.environment.Environment

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            App(
                environment = getEnvironmentFromBuildConfig(),
                deviceConfig = AndroidDeviceConfig(),
            )
        }
    }

    @Suppress("KotlinConstantConditions")
    private fun getEnvironmentFromBuildConfig(): Environment {
        return when (BuildConfig.FLAVOR) {
            "dev" -> Environment.DEV
            "beta" -> Environment.BETA
            "prod" -> Environment.PROD
            else -> Environment.DEV
        }
    }
}