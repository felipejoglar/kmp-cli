package kmp.cli.util

import okio.Path

expect class ProcessRunner(logger: Logger, pretend: Boolean = false) {
    fun run(command: List<String>, workingDir: Path, successMessage: String)
    fun runPassthrough(command: List<String>, silent: Boolean = false)
}
