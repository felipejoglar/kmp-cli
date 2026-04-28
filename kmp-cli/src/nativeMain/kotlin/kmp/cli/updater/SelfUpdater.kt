package kmp.cli.updater

import platform.posix.pclose
import platform.posix.popen
import platform.posix.fgets
import kotlinx.cinterop.refTo
import kotlinx.cinterop.toKString
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
actual fun captureProcess(command: List<String>): String {
    val shellCommand = command.joinToString(" ") { arg ->
        if (arg.contains(' ') || arg.contains('\'')) "'${arg.replace("'", "'\\''")}'" else arg
    }

    val pipe = popen("$shellCommand 2>&1", "r")
        ?: throw RuntimeException("Failed to execute: ${command.first()}")

    val output = StringBuilder()
    val buf = ByteArray(1024)
    while (fgets(buf.refTo(0), buf.size, pipe) != null) {
        output.append(buf.toKString())
    }

    val status = pclose(pipe)
    val exitCode = status shr 8
    if (exitCode != 0) {
        throw RuntimeException("${command.first()} failed (exit code $exitCode):\n$output")
    }

    return output.toString()
}
