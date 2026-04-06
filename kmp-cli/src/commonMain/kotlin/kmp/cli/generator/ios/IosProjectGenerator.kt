package kmp.cli.generator.ios

import okio.FileSystem
import okio.Path
import kmp.cli.util.Logger

class IosProjectGenerator(
    private val projectName: String,
    private val logger: Logger,
) {

    fun generateProject(projectDir: Path) {
        val iosDir = projectDir / "apps" / "ios" / projectName
        if (!FileSystem.SYSTEM.exists(iosDir)) {
            logger.warning("iOS directory not found, skipping iOS project")
            return
        }

        val xcodeproj = iosDir / "$projectName.xcodeproj"
        if (FileSystem.SYSTEM.exists(xcodeproj)) {
            logger.info("iOS project already generated: ${xcodeproj.name}")
        }
    }
}
