package com.example.kmptemplate

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kmptemplate.application.config.device.DeviceConfig
import com.example.kmptemplate.application.config.environment.Environment
import com.example.kmptemplate.ui.theme.KmpTemplateTheme
import com.example.kmptemplate.ui.tools.PreviewLandscape
import com.example.kmptemplate.ui.tools.PreviewLightDark

@Composable
fun App(
    environment: Environment,
    deviceConfig: DeviceConfig,
) {
    KmpTemplateTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "🎉",
                    style = MaterialTheme.typography.displayLarge,
                )
                
                Spacer(Modifier.height(16.dp))
                
                Text(
                    text = "Kmp Template",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                
                Spacer(Modifier.height(8.dp))
                
                Text(
                    text = "You're running on ${deviceConfig.platform} ${deviceConfig.osVersion}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
@PreviewLightDark
@PreviewLandscape
private fun AppPreview() {
    App(
        environment = Environment.DEV,
        deviceConfig = PreviewDeviceConfig(),
    )
}

private class PreviewDeviceConfig : DeviceConfig {
    override val appVersion: String
        get() = "1.0.0"
    override val osVersion: String
        get() = "16(36)"
    override val platform: String
        get() = "Android"
    override val device: String
        get() = "Google Pixel 8a"
    override val isDebug: Boolean
        get() = true
}

