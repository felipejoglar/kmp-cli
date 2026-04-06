package kmp.cli.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.validate
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.validate
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import okio.Path.Companion.toPath
import kmp.cli.generator.ProjectGenerator
import kmp.cli.generator.template.ProjectNameConverter
import kmp.cli.platform.currentDir

class NewCommand : CliktCommand(name = "new") {
    override fun help(context: Context) = "Create a new Kotlin Multiplatform application"

    override fun helpEpilog(context: Context) = """
        |Description:
        |    The `kmp-cli new` command creates a new Kotlin Multiplatform application
        |    with a default directory structure and configuration at the path you specify.
        |
        |    You can specify either a project name or a full path. The last segment of
        |    the path is used as the project name.
        |
        |Examples:
        |    kmp-cli new MyApp
        |    kmp-cli new ~/projects/MyApp
        |    kmp-cli new MyApp --package=com.mycompany --skip-ios
    """.trimMargin()

    private val appPath by argument(name = "APP_PATH").validate {
        val name = it.toPath().name
        require(name.isNotEmpty()) { "APP_PATH must not be empty" }
        val pascal = ProjectNameConverter.toPascalCase(name)
        require(pascal.isNotEmpty()) { "APP_PATH '$name' cannot be converted to a valid project name" }
        require(pascal.first().isUpperCase()) { "APP_PATH '$name' must produce a name starting with a letter, got '$pascal'" }
        require(pascal.all { c -> c.isLetterOrDigit() }) { "APP_PATH '$name' contains invalid characters for a project name" }
    }
    private val packageBase by option("--package", metavar = "PACKAGE", help = "Base package name (e.g., com.example)").default("com.example").validate {
        val segments = it.split(".")
        require(segments.all { s -> s.isNotEmpty() }) { "Package name must not contain empty segments" }
        require(segments.all { s -> s.first().isLetter() }) { "Each package segment must start with a letter" }
        require(segments.all { s -> s.all { c -> c.isLetterOrDigit() || c == '_' } }) { "Package segments may only contain letters, digits, and underscores" }
    }
    private val skipGit by option("-G", "--skip-git", help = "Skip git init").flag()
    private val skipDeps by option("--skip-deps", help = "Skip running gradle assemble after generation").flag()
    private val skipIos by option("--skip-ios", help = "Skip iOS project generation").flag()
    private val runtime by NewCommandOptions()

    override fun run() {
        val inputPath = appPath.toPath()
        val resolvedPath = if (inputPath.isAbsolute) inputPath else currentDir() / inputPath
        val dirName = resolvedPath.name
        val projectName = ProjectNameConverter.toPascalCase(dirName)
        val targetDir = resolvedPath.parent ?: currentDir()

        if (!runtime.quiet) echo("Creating new KMP project: $projectName")
        val generator = ProjectGenerator(
            projectName = projectName,
            packageBase = packageBase,
            targetDir = targetDir,
            dirName = dirName,
            gitInit = !skipGit,
            installDeps = !skipDeps,
            generateIos = !skipIos,
            force = runtime.force,
            pretend = runtime.pretend,
            quiet = runtime.quiet
        )
        generator.generate()
    }
}
