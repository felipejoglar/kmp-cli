package kmp.cli.util

import okio.Path

class Logger(
    private val quiet: Boolean = false,
    private val pretend: Boolean = false,
) {
    private var baseDir: Path? = null

    fun setBaseDir(dir: Path) {
        baseDir = dir
    }

    private fun relativePath(path: Path): String {
        val base = baseDir ?: return path.toString()
        return try {
            path.relativeTo(base).toString()
        } catch (_: Exception) {
            path.toString()
        }
    }

    private fun printAction(action: String, detail: String, color: String) {
        if (quiet) return
        val label = action.padStart(LABEL_WIDTH)
        val prefix = if (pretend) "[pretend] " else ""
        println("$color$label$RESET  $prefix$detail")
    }

    fun create(path: Path) = printAction("create", relativePath(path), GREEN)
    fun remove(path: Path) = printAction("remove", relativePath(path), RED)
    fun rename(from: Path, to: Path) = printAction("rename", "${relativePath(from)} → ${relativePath(to)}", YELLOW)
    fun skip(path: Path) = printAction("skip", relativePath(path), YELLOW)
    fun exist(path: Path) = printAction("exist", relativePath(path), BLUE)
    fun force(path: Path) = printAction("force", relativePath(path), YELLOW)
    fun update(path: Path) = printAction("update", relativePath(path), GREEN)
    fun chmod(path: Path) = printAction("chmod", relativePath(path), GREEN)
    fun run(command: String) = printAction("run", command, CYAN)
    fun warning(message: String) = printAction("warning", message, YELLOW)
    fun info(message: String) {
        if (!quiet) println(message)
    }

    companion object {
        private const val LABEL_WIDTH = 12
        private const val RESET = "\u001B[0m"
        private const val GREEN = "\u001B[32m"
        private const val RED = "\u001B[31m"
        private const val YELLOW = "\u001B[33m"
        private const val CYAN = "\u001B[36m"
        private const val BLUE = "\u001B[34m"
    }
}
