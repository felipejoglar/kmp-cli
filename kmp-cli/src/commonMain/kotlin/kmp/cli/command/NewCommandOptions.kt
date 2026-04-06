package kmp.cli.command

import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option

class NewCommandOptions : OptionGroup(name = "Runtime options") {
    val force by option("-f", "--force", help = "Overwrite files that already exist").flag()
    val pretend by option("-p", "--pretend", help = "Run but do not make any changes").flag()
    val quiet by option("-q", "--quiet", help = "Suppress status output").flag()
}
