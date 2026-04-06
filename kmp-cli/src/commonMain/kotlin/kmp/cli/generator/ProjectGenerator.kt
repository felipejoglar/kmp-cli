package kmp.cli.generator

import okio.FileSystem
import okio.Path
import okio.Path.Companion.DIRECTORY_SEPARATOR
import okio.Path.Companion.toPath
import kmp.cli.generator.ios.IosProjectGenerator
import kmp.cli.generator.template.TemplateEngine
import kmp.cli.platform.getenv
import kmp.cli.platform.homeDir
import kmp.cli.platform.osName
import kmp.cli.platform.setExecutable
import kmp.cli.util.Logger
import kmp.cli.util.ProcessRunner

class ProjectGenerator(
    private val projectName: String,
    private val packageBase: String,
    private val targetDir: Path,
    private val dirName: String = projectName,
    private val gitInit: Boolean,
    private val installDeps: Boolean,
    private val generateIos: Boolean,
    private val force: Boolean = false,
    private val pretend: Boolean = false,
    private val quiet: Boolean = false
) {
    private val moduleName = projectName.lowercase()
    private val frameworkName = "${projectName}Kit"
    private val displayName = projectName.replace(Regex("(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])"), " ")

    private val logger = Logger(quiet, pretend)
    private val shell = ProcessRunner(logger, pretend)
    private val templateEngine = TemplateEngine(logger, pretend)
    private val iosGenerator = IosProjectGenerator(projectName, logger)

    fun generate() {
        val projectDir = targetDir / dirName
        logger.setBaseDir(targetDir)

        if (FileSystem.SYSTEM.exists(projectDir)) {
            if (force) {
                if (!pretend) FileSystem.SYSTEM.deleteRecursively(projectDir)
                logger.force(projectDir)
            } else {
                throw IllegalArgumentException("Directory $projectDir already exists. Use --force to overwrite.")
            }
        }
        if (!pretend) {
            FileSystem.SYSTEM.createDirectories(projectDir)
        }
        logger.create(projectDir)

        val packagePath = packageBase.replace(".", DIRECTORY_SEPARATOR)

        val pathMapping = mutableMapOf(
            "KmpTemplateApp" to "${projectName}App",
            "KmpTemplate" to projectName,
            "kmptemplate" to moduleName,
            "com${DIRECTORY_SEPARATOR}example" to packagePath,
        )

        val skipPatterns = mutableSetOf<String>()
        if (!generateIos) {
            skipPatterns.add("ios")
        }

        val templateRoot = resolveTemplateRoot()
        templateEngine.copyTemplate(templateRoot, projectDir, pathMapping, skipPatterns)
        if (!pretend) {
            setExecPermissions(projectDir)
        } else {
            logger.chmod(projectDir / "gradlew")
        }

        templateEngine.replacePlaceholders(projectDir, contentPlaceholders())

        if (generateIos) {
            iosGenerator.generateProject(projectDir)
        }

        if (gitInit) {
            shell.run(listOf("git", "init"), projectDir, "Git repository initialized")
        }

        if (!pretend) {
            writeLocalProperties(projectDir)
        }

        if (installDeps) {
            shell.run(listOf("./gradlew", "assemble"), projectDir, "Dependencies installed")
        }
    }

    private fun resolveTemplateRoot(): Path {
        val executablePath = kmp.cli.platform.executablePath()
        val templatesDir = executablePath.parent!! / "templates" / "kmp-template"
        if (FileSystem.SYSTEM.exists(templatesDir)) return templatesDir

        val libTemplates = executablePath.parent!!.parent!! / "lib" / "templates" / "kmp-template"
        if (FileSystem.SYSTEM.exists(libTemplates)) return libTemplates

        throw IllegalStateException(
            "Template directory not found. Looked in:\n  $templatesDir\n  $libTemplates"
        )
    }

    private fun writeLocalProperties(projectDir: Path) {
        val sdkDir = getenv("ANDROID_HOME")
            ?: getenv("ANDROID_SDK_ROOT")
            ?: findDefaultSdkPath()
            ?: return

        val localProps = projectDir / "local.properties"
        FileSystem.SYSTEM.write(localProps) {
            writeUtf8("sdk.dir=${sdkDir.replace("\\", "\\\\")}\n")
        }
        logger.create(localProps)
    }

    private fun findDefaultSdkPath(): String? {
        val os = osName()
        val home = homeDir() ?: return null
        val candidates = when {
            "mac" in os || "macos" in os -> listOf("$home/Library/Android/sdk")
            "win" in os || "mingw" in os -> listOf("${getenv("LOCALAPPDATA")}\\Android\\Sdk")
            else -> listOf("$home/Android/Sdk")
        }
        return candidates.firstOrNull { path -> FileSystem.SYSTEM.exists(path.toPath()) }
    }

    private fun contentPlaceholders(): Map<String, String> {
        val entries = listOf(
            "com.example.kmptemplate.ios.dev" to "$packageBase.ios.dev",
            "com.example.kmptemplate.ios.beta" to "$packageBase.ios.beta",
            "com.example.kmptemplate.ios" to "$packageBase.ios",
            "com.example.kmptemplate" to packageBase,
            "KmpTemplateKit" to frameworkName,
            "KmpTemplate" to projectName,
            "Kmp Template" to displayName,
            "kmptemplate" to moduleName,
        )
        return linkedMapOf(*entries.toTypedArray())
    }

    private fun setExecPermissions(target: Path) {
        val gradlew = target / "gradlew"
        if (FileSystem.SYSTEM.exists(gradlew)) {
            setExecutable(gradlew)
            logger.chmod(gradlew)
        }
    }
}
