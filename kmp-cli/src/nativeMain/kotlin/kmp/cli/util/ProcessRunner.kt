package kmp.cli.util

import okio.Path
import platform.posix.pclose
import platform.posix.popen
import platform.posix.fgets
import platform.posix.chdir
import platform.posix.getcwd
import platform.posix.system
import kotlinx.cinterop.refTo
import kotlinx.cinterop.toKString
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
actual class ProcessRunner actual constructor(private val logger: Logger, private val pretend: Boolean) {

    actual fun run(command: List<String>, workingDir: Path, successMessage: String) {
        logger.run(command.joinToString(" ") + " from ${workingDir.name}")
        if (pretend) return

        val buf = ByteArray(4096)
        val oldDir = getcwd(buf.refTo(0), buf.size.toULong())?.toKString()
        chdir(workingDir.toString())

        val shellCommand = command.joinToString(" ") { arg ->
            if (arg.contains(' ') || arg.contains('\'')) "'${arg.replace("'", "'\\''")}'" else arg
        }
        val fullCommand = "$shellCommand 2>&1"

        val pipe = popen(fullCommand, "r")
            ?: throw RuntimeException("'${command.first()}' not found. Please make sure it is installed and available on PATH.")

        val lines = ArrayDeque<String>(50)
        val readBuf = ByteArray(1024)
        while (fgets(readBuf.refTo(0), readBuf.size, pipe) != null) {
            val line = readBuf.toKString().trimEnd('\n', '\r')
            if (lines.size >= 50) lines.removeFirst()
            lines.addLast(line)
        }

        val status = pclose(pipe)
        val exitCode = status shr 8

        if (oldDir != null) chdir(oldDir)

        if (exitCode != 0) {
            throw RuntimeException(
                "${command.first()} failed (exit code $exitCode):\n${lines.joinToString("\n")}"
            )
        }
        logger.info("      $successMessage")
    }

    actual fun runPassthrough(command: List<String>, silent: Boolean) {
        if (!silent) logger.run(command.joinToString(" "))
        if (pretend) return

        val shellCommand = command.joinToString(" ") { arg ->
            if (arg.contains(' ') || arg.contains('\'')) "'${arg.replace("'", "'\\''")}'" else arg
        }

        val status = system(shellCommand)
        val exitCode = status shr 8
        if (exitCode != 0) {
            throw RuntimeException("${command.first()} failed (exit code $exitCode)")
        }
    }
}
