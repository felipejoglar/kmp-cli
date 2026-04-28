package kmp.cli.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.clikt.parameters.options.option
import kmp.cli.updater.SelfUpdater

class UpdateCommand : CliktCommand(name = "update") {
    override fun help(context: Context) = "Update KMP CLI to the latest version"

    override fun helpEpilog(context: Context) = """
        |Description:
        |    The `kmp-cli update` command updates the KMP CLI binary to the latest
        |    release from GitHub. It downloads the correct archive for your platform,
        |    verifies the checksum, and replaces the running binary.
        |
        |    If you are already on the latest version, the command exits without
        |    making changes. Use --force to reinstall anyway.
        |
        |Examples:
        |    kmp-cli update
        |    kmp-cli update --version=1.2.0
        |    kmp-cli update --force
    """.trimMargin()

    private val version by option("--version", metavar = "VERSION", help = "Update to a specific version")
    private val runtime by RuntimeOptions()

    override fun run() {
        val updater = SelfUpdater(
            targetVersion = version,
            force = runtime.force,
            pretend = runtime.pretend,
            quiet = runtime.quiet,
        )
        updater.update()
    }
}
