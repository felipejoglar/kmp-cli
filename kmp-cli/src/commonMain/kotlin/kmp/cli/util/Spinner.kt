package kmp.cli.util

expect class Spinner(quiet: Boolean = false) {
    fun start(message: String)
    fun stop(success: Boolean = true)
}
