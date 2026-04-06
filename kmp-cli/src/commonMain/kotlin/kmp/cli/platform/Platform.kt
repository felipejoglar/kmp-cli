package kmp.cli.platform

import okio.Path

expect fun getenv(name: String): String?

expect fun currentDir(): Path

expect fun homeDir(): String?

expect fun osName(): String

expect fun setExecutable(path: Path)

expect fun executablePath(): Path
