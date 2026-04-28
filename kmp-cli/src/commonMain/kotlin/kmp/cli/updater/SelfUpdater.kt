package kmp.cli.updater

import kotlin.random.Random

import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import kmp.cli.BuildConfig
import kmp.cli.platform.executablePath
import kmp.cli.platform.osName
import kmp.cli.platform.setExecutable
import kmp.cli.util.Logger
import kmp.cli.util.ProcessRunner
import kmp.cli.util.Spinner

class SelfUpdater(
    private val targetVersion: String?,
    private val force: Boolean,
    private val pretend: Boolean,
    private val quiet: Boolean,
) {
    private val logger = Logger(quiet, pretend)
    private val shell = ProcessRunner(logger, pretend)
    private val spinner = Spinner(quiet)

    private val repo = "felipejoglar/kmp-cli"

    fun update() {
        val resolvedVersion = resolveVersion()
        val current = BuildConfig.VERSION

        if (!quiet) {
            logger.info("     current  $current")
            logger.info("      latest  $resolvedVersion")
            logger.info("")
        }

        if (current == resolvedVersion && !force) {
            logger.info("KMP CLI is already up to date ($current).")
            return
        }

        if (current == resolvedVersion && force) {
            logger.warning("reinstalling same version ($current)")
        }

        val archive = archiveName()
        val tmpDir = "/tmp/kmp-cli-update-${Random.nextInt(100000, 999999)}".toPath()

        try {
            download(tmpDir, archive, resolvedVersion)
            verifyChecksum(tmpDir, archive)
            extractAndReplace(tmpDir, archive)

            if (!quiet) {
                logger.info("")
                logger.info("KMP CLI updated successfully: $current → $resolvedVersion")
            }
        } finally {
            cleanup(tmpDir)
        }
    }

    private fun resolveVersion(): String {
        if (targetVersion != null) return targetVersion

        spinner.start("Checking for updates...")
        try {
            val version = resolveLatestVersion()
            spinner.stop(success = true)
            return version
        } catch (e: Exception) {
            spinner.stop(success = false)
            throw e
        }
    }

    private fun resolveLatestVersion(): String {
        if (pretend) return "0.0.0"

        val result = captureCommand(
            listOf(
                "curl", "-fsSI",
                "https://github.com/$repo/releases/latest"
            )
        )
        val locationLine = result.lines().firstOrNull { it.startsWith("location:", ignoreCase = true) }
            ?: throw RuntimeException("Could not determine the latest release version. Check https://github.com/$repo/releases")

        val version = Regex("""/v?(\d[^ /\r]*)""").find(locationLine)?.groupValues?.get(1)
            ?: throw RuntimeException("Could not parse version from redirect: $locationLine")

        return version
    }

    private fun archiveName(): String {
        val os = osName()
        val platform = when {
            "mac" in os || "macos" in os -> "darwin"
            "linux" in os -> "linux"
            else -> throw RuntimeException("Unsupported OS: $os")
        }
        val arch = detectArch()
        return "kmp-cli-$platform-$arch.tar.gz"
    }

    private fun detectArch(): String {
        if (pretend) return "arm64"
        val uname = captureCommand(listOf("uname", "-m")).trim()
        return when (uname) {
            "arm64", "aarch64" -> "arm64"
            "x86_64" -> "amd64"
            else -> throw RuntimeException("Unsupported architecture: $uname")
        }
    }

    private fun download(tmpDir: Path, archive: String, version: String) {
        if (!pretend) {
            FileSystem.SYSTEM.createDirectories(tmpDir)
        }

        val archiveUrl = "https://github.com/$repo/releases/download/v$version/$archive"
        val checksumsUrl = "https://github.com/$repo/releases/download/v$version/checksums.txt"

        if (!quiet) logger.info(" downloading  $archive")
        shell.runPassthrough(
            listOf("curl", "-fSL", "--progress-bar", archiveUrl, "-o", "$tmpDir/$archive"),
            silent = true,
        )

        spinner.start("Downloading checksums...")
        try {
            captureCommand(listOf("curl", "-fsSL", checksumsUrl, "-o", "$tmpDir/checksums.txt"))
            spinner.stop(success = true)
        } catch (e: Exception) {
            spinner.stop(success = false)
            throw e
        }
    }

    private fun verifyChecksum(tmpDir: Path, archive: String) {
        spinner.start("Verifying checksum...")
        try {
            if (pretend) {
                spinner.stop(success = true)
                return
            }

            val checksumsContent = captureCommand(listOf("cat", "$tmpDir/checksums.txt"))
            val expectedLine = checksumsContent.lines().firstOrNull { archive in it }
                ?: throw RuntimeException("Archive $archive not found in checksums.txt")
            val expected = expectedLine.trim().split(Regex("\\s+")).first()

            val actual = captureCommand(listOf("shasum", "-a", "256", "$tmpDir/$archive"))
                .trim().split(Regex("\\s+")).first()

            if (expected != actual) {
                throw RuntimeException(
                    "Checksum verification failed!\n  Expected: $expected\n  Actual:   $actual"
                )
            }
            spinner.stop(success = true)
        } catch (e: Exception) {
            spinner.stop(success = false)
            throw e
        }
    }

    private fun extractAndReplace(tmpDir: Path, archive: String) {
        spinner.start("Installing...")
        try {
            val extractDir = tmpDir / "extracted"
            if (!pretend) {
                FileSystem.SYSTEM.createDirectories(extractDir)
                captureCommand(
                    listOf("tar", "-xzf", "$tmpDir/$archive", "-C", extractDir.toString(), "--strip-components=1")
                )
            }

            val binaryPath = executablePath()
            val newBinary = extractDir / "bin" / "kmp-cli"
            val templatesSource = extractDir / "bin" / "templates"
            val templatesTarget = (binaryPath.parent ?: throw RuntimeException("Cannot determine installation directory from binary path: $binaryPath")) / "templates"

            if (!pretend) {
                FileSystem.SYSTEM.atomicMove(newBinary, binaryPath)
                setExecutable(binaryPath)

                if (FileSystem.SYSTEM.exists(templatesSource)) {
                    if (FileSystem.SYSTEM.exists(templatesTarget)) {
                        FileSystem.SYSTEM.deleteRecursively(templatesTarget)
                    }
                    captureCommand(
                        listOf("cp", "-R", templatesSource.toString(), templatesTarget.toString())
                    )
                }
            }
            spinner.stop(success = true)
        } catch (e: Exception) {
            spinner.stop(success = false)
            throw e
        }
    }

    private fun cleanup(tmpDir: Path) {
        if (pretend) return
        try {
            if (FileSystem.SYSTEM.exists(tmpDir)) {
                FileSystem.SYSTEM.deleteRecursively(tmpDir)
            }
        } catch (_: Exception) {
            // best-effort cleanup
        }
    }

    private fun captureCommand(command: List<String>): String {
        if (pretend) return ""
        return captureProcess(command)
    }
}

expect fun captureProcess(command: List<String>): String
