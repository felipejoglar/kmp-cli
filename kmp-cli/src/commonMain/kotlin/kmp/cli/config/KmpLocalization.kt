package kmp.cli.config

import com.github.ajalt.clikt.output.Localization

class KmpLocalization : Localization {
    override fun helpOptionMessage(): String = "Show help"
}
