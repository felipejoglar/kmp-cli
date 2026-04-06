package com.example.kmptemplate.android.application.config.device

import android.os.Build
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import com.example.kmptemplate.android.BuildConfig
import com.example.kmptemplate.application.config.device.DeviceConfig

class AndroidDeviceConfig : DeviceConfig {
    override val appVersion: String
        get() = BuildConfig.VERSION_NAME

    override val osVersion: String
        get() = "${Build.VERSION.RELEASE}(${Build.VERSION.SDK_INT})"

    override val platform: String
        get() = "Android"

    override val device: String
        get() = "${Build.BRAND.capitalize(Locale.current)} ${Build.MODEL}"

    override val isDebug: Boolean
        get() = BuildConfig.DEBUG
}