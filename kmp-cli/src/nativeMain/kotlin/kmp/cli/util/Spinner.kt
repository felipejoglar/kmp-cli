package kmp.cli.util

import platform.posix.fflush
import platform.posix.stdout
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
actual class Spinner actual constructor(private val quiet: Boolean) {
    private val frames = charArrayOf('⠋', '⠙', '⠹', '⠸', '⠼', '⠴', '⠦', '⠧', '⠇', '⠏')
    private var message: String = ""

    actual fun start(message: String) {
        if (quiet) return
        this.message = message
        print("\r\u001B[2K $CYAN${frames[0]}$RESET $message")
        fflush(stdout)
    }

    actual fun stop(success: Boolean) {
        if (quiet) return
        val icon = if (success) "$GREEN✔$RESET" else "$RED✖$RESET"
        print("\r\u001B[2K $icon $message\n")
        fflush(stdout)
    }

    companion object {
        private const val RESET = "\u001B[0m"
        private const val GREEN = "\u001B[32m"
        private const val RED = "\u001B[31m"
        private const val CYAN = "\u001B[36m"
    }
}
