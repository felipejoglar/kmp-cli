package kmp.cli.generator.template

object ProjectNameConverter {

    fun toPascalCase(input: String): String {
        return input
            .replace(Regex("[_.\\-\\s]+"), " ")
            .replace(Regex("([a-z])([A-Z])"), "$1 $2")
            .replace(Regex("([A-Z]+)([A-Z][a-z])"), "$1 $2")
            .split(" ")
            .filter { it.isNotEmpty() }
            .joinToString("") { it.lowercase().replaceFirstChar { c -> c.uppercase() } }
    }
}
