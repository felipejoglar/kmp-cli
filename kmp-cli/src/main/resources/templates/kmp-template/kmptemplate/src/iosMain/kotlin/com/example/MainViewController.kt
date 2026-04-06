package com.example.kmptemplate

import androidx.compose.ui.window.ComposeUIViewController
import com.example.kmptemplate.application.config.device.DeviceConfig
import com.example.kmptemplate.application.config.environment.Environment

fun MainViewController(
    environment: Environment,
    deviceConfig: DeviceConfig,
) = ComposeUIViewController {
    App(
        environment = environment,
        deviceConfig = deviceConfig,
    )
}