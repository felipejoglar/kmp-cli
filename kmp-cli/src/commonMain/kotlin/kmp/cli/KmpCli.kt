package kmp.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.parameters.options.versionOption
import kmp.cli.config.CliHelpFormatter
import kmp.cli.config.KmpLocalization

class KmpCli : CliktCommand(name = "kmp-cli") {
    init {
        context {
            helpOptionNames = setOf("--help", "-h")
            localization = KmpLocalization()
            helpFormatter = { CliHelpFormatter() }
        }
        versionOption(BuildConfig.VERSION, names = setOf("--version", "-v"), help = "Show version number", message = { "KMP CLI $it" })
    }
    override fun run() = Unit
}
