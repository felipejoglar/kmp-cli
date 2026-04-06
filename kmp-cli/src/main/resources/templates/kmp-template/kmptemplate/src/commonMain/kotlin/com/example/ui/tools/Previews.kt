package com.example.kmptemplate.ui.tools

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

/**
 * A MultiPreview annotation for displaying a @[Composable] method using seven standard font sizes.
 */
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.FUNCTION)
@Preview(name = "85%", fontScale = 0.85f)
@Preview(name = "100%", fontScale = 1.0f)
@Preview(name = "115%", fontScale = 1.15f)
@Preview(name = "130%", fontScale = 1.3f)
@Preview(name = "150%", fontScale = 1.5f)
@Preview(name = "180%", fontScale = 1.8f)
@Preview(name = "200%", fontScale = 2f)
annotation class PreviewFontScale

/**
 * A MultiPreview annotation for displaying a @[Composable] method using light and dark themes.
 *
 * Note that the app theme should support dark and light modes for these previews to be different.
 */
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.FUNCTION)
@Preview(name = "Light", device = Devices.PHONE,)
@Preview(name = "Dark", device = Devices.PHONE, uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL)
annotation class PreviewLightDark


@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.FUNCTION)
@Preview(name = "Landscape", device = Devices.PHONE, widthDp = 891, heightDp = 411)
annotation class PreviewLandscape