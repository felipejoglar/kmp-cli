package kmp.cli.platform

import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import platform.posix.getenv
import kotlinx.cinterop.toKString
import platform.posix.system

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
actual fun getenv(name: String): String? = getenv(name)?.toKString()

actual fun currentDir(): Path = FileSystem.SYSTEM.canonicalize(".".toPath())

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
actual fun homeDir(): String? = getenv("HOME")?.toKString()

@OptIn(kotlin.experimental.ExperimentalNativeApi::class)
actual fun osName(): String {
    return Platform.osFamily.name.lowercase()
}

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
actual fun setExecutable(path: Path) {
    system("chmod 755 \"${path.toString()}\"")
}

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
actual fun executablePath(): Path {
    // Resolve via /proc/self/exe (Linux) or argv[0] via _ env var
    val procSelf = "/proc/self/exe".toPath()
    if (FileSystem.SYSTEM.exists(procSelf)) {
        return FileSystem.SYSTEM.canonicalize(procSelf)
    }
    // Fallback: use _ environment variable (set by shell on macOS/Linux)
    val argv0 = getenv("_")?.toKString()
    if (argv0 != null) {
        val path = argv0.toPath()
        return if (path.isAbsolute) path else FileSystem.SYSTEM.canonicalize(path)
    }
    return currentDir()
}
