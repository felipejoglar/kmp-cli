package kmp.cli.config

import com.github.ajalt.clikt.output.HelpFormatter

class CliHelpFormatter : HelpFormatter {
    
    private fun flattenParameters(parameters: List<HelpFormatter.ParameterHelp>): List<Pair<String, String>> {
        val result = mutableListOf<Pair<String, String>>()
        for (section in parameters) {
            when (section) {
                is HelpFormatter.ParameterHelp.Option -> {
                    val allNames = (section.names + section.secondaryNames).sortedBy { it.length }
                    val hasShortName = allNames.any { it.startsWith("-") && !it.startsWith("--") }
                    val nameStr = allNames.joinToString(", ")
                    val prefix = if (hasShortName) "  " else "      "

                    val isFlag = section.nvalues.isEmpty()
                    val metaStr = if (!isFlag && section.metavar != null) "=${section.metavar}" else ""
                    val defaultStr = if (isFlag) "" else section.tags["default"]?.let {
                        if (it.isNotEmpty()) " [default: $it]" else ""
                    } ?: ""
                    result.add("$prefix$nameStr$metaStr" to (section.help + defaultStr))
                }

                is HelpFormatter.ParameterHelp.Subcommand -> {
                    result.add("  ${section.name}" to section.help)
                }

                is HelpFormatter.ParameterHelp.Argument -> {
                    result.add("  ${section.name}" to section.help)
                }

                is HelpFormatter.ParameterHelp.Group -> {
                    result.add("${section.name}  ${section.help}" to "")
                }
            }
        }
        return result
    }

    override fun formatHelp(
        error: com.github.ajalt.clikt.core.UsageError?,
        prolog: String,
        epilog: String,
        parameters: List<HelpFormatter.ParameterHelp>,
        programName: String
    ): String {
        val commands = parameters.filterIsInstance<HelpFormatter.ParameterHelp.Subcommand>()
        val arguments = parameters.filterIsInstance<HelpFormatter.ParameterHelp.Argument>()
        val options =
            parameters.filter { it !is HelpFormatter.ParameterHelp.Subcommand && it !is HelpFormatter.ParameterHelp.Argument }

        val builder = StringBuilder()
        builder.append("\n")

        if (error != null) {
            builder.append(error.message)
            builder.append("\n\n")
        }

        builder.append(asciiArt)

        val argNames = arguments.joinToString(" ") { it.name }
        val usageLine = when {
            argNames.isNotEmpty() -> "  $programName $argNames [options]"
            commands.isNotEmpty() -> "  $programName COMMAND [options]"
            else -> "  $programName [options]"
        }
        builder.append("\n\nUsage:\n$usageLine\n")

        if (commands.isNotEmpty()) {
            val commandItems = flattenParameters(commands)
            val maxWidth = commandItems.maxOfOrNull { it.first.length } ?: 0
            builder.append("\nCommands:\n")
            for ((name, help) in commandItems) {
                builder.append(name)
                if (help.isNotEmpty()) {
                    val padding = " ".repeat(maxWidth - name.length + 2)
                    builder.append(padding)
                    builder.append(help)
                }
                builder.append("\n")
            }
            builder.append("\nAll commands can be run with -h (or --help) for more information.\n")
        }

        if (options.isNotEmpty()) {
            val groupNames = options.filterIsInstance<HelpFormatter.ParameterHelp.Option>()
                .mapNotNull { it.groupName }.toSet()

            val ungroupedOptions = options.filter {
                it !is HelpFormatter.ParameterHelp.Group &&
                        (it !is HelpFormatter.ParameterHelp.Option || it.groupName !in groupNames)
            }
            val allItems = flattenParameters(options)
            val maxWidth = allItems.maxOfOrNull { it.first.length } ?: 0

            if (ungroupedOptions.isNotEmpty()) {
                builder.append("\nOptions:\n")
                for ((name, help) in flattenParameters(ungroupedOptions)) {
                    builder.append(name)
                    if (help.isNotEmpty()) {
                        val padding = " ".repeat(maxWidth - name.length + 2)
                        builder.append(padding)
                        builder.append(help)
                    }
                    builder.append("\n")
                }
            }

            for (groupName in groupNames) {
                val groupOptions =
                    options.filter { it is HelpFormatter.ParameterHelp.Option && it.groupName == groupName }
                if (groupOptions.isNotEmpty()) {
                    builder.append("\n$groupName:\n")
                    for ((name, help) in flattenParameters(groupOptions)) {
                        builder.append(name)
                        if (help.isNotEmpty()) {
                            val padding = " ".repeat(maxWidth - name.length + 2)
                            builder.append(padding)
                            builder.append(help)
                        }
                        builder.append("\n")
                    }
                }
            }
        }

        if (epilog.isNotEmpty()) {
            builder.append("\n")
            builder.append(epilog)
        }

        return builder.toString().trimEnd()
    }

    companion object {
        private val asciiArt = """
            ██╗  ██╗███╗   ███╗██████╗      ██████╗██╗     ██╗
            ██║ ██╔╝████╗ ████║██╔══██╗    ██╔════╝██║     ██║
            █████╔╝ ██╔████╔██║██████╔╝    ██║     ██║     ██║
            ██╔═██╗ ██║╚██╔╝██║██╔═══╝     ██║     ██║     ██║
            ██║  ██╗██║ ╚═╝ ██║██║         ╚██████╗███████╗██║
            ╚═╝  ╚═╝╚═╝     ╚═╝╚═╝          ╚═════╝╚══════╝╚═╝
        """.trimIndent()
    }
}
