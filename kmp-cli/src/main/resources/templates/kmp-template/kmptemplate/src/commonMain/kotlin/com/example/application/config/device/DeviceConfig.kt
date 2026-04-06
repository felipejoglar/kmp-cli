package com.example.kmptemplate.application.config.device

/**
 * Contract for accessing device-specific configuration information.
 */
interface DeviceConfig {
    /**
     * The version of the application.
     * This is typically the version string defined in the application (e.g., "1.0.0", "2.1-beta").
     */
    val appVersion: String

    /**
     * The version of the operating system running on the device.
     * For example, "15.0" for iOS or "12" for Android.
     */
    val osVersion: String

    /**
     * The platform the application is running on.
     * Common values include "Android", "iOS".
     */
    val platform: String

    /**
     * A string identifying the specific device model.
     * For example, "google,Pixel 6", "iPhone 13 Pro".
     */
    val device: String

    /**
     * A boolean flag indicating whether the current build of the application is a debug build.
     * This is typically true during development and false for release builds.
     */
    val isDebug: Boolean
}
