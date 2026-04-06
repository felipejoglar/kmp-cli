package kmp.cli.generator.template

import okio.FileSystem
import okio.Path
import okio.buffer
import kmp.cli.util.Logger

class TemplateEngine(private val logger: Logger, private val pretend: Boolean = false) {

    private val excludedPatterns = setOf(
        ".gradle", ".DS_Store", "build", "local.properties", "xcuserdata",
    )

    fun copyTemplate(templateRoot: Path, target: Path, pathMapping: Map<String, String>, skipPatterns: Set<String> = emptySet()) {
        copyTree(templateRoot, target, pathMapping, skipPatterns)
        renameDotfiles(target)
    }

    private fun renameDotfiles(target: Path) {
        val gitignore = target / "gitignore"
        val dotGitignore = target / ".gitignore"
        if (pretend) {
            logger.rename(gitignore, dotGitignore)
            return
        }
        if (FileSystem.SYSTEM.exists(gitignore)) {
            FileSystem.SYSTEM.atomicMove(gitignore, dotGitignore)
            logger.update(dotGitignore)
        }
    }

    private fun shouldExclude(relativePath: Path, skipPatterns: Set<String>): Boolean {
        val segments = mutableListOf<String>()
        var current: Path? = relativePath
        while (current != null && current.name.isNotEmpty()) {
            segments.add(current.name)
            current = current.parent
        }
        return segments.any { name ->
            name in excludedPatterns || skipPatterns.any { pattern ->
                name == pattern || name.startsWith(pattern, ignoreCase = true) || name.endsWith(pattern, ignoreCase = true)
            }
        }
    }

    private fun transformPath(dest: Path, root: Path, pathMapping: Map<String, String>): Path {
        val relative = dest.relativeTo(root)
        var transformed = relative.toString()
        for ((placeholder, replacement) in pathMapping) {
            transformed = transformed.replace(placeholder, replacement)
        }
        return root / transformed
    }

    private fun copyTree(source: Path, target: Path, pathMapping: Map<String, String>, skipPatterns: Set<String>) {
        if (pretend) {
            walkAndLog(source, target, pathMapping, skipPatterns)
            return
        }
        FileSystem.SYSTEM.listRecursively(source).forEach { src ->
            val relative = src.relativeTo(source)
            if (shouldExclude(relative, skipPatterns)) return@forEach
            val metadata = FileSystem.SYSTEM.metadata(src)
            if (!metadata.isDirectory) {
                val dest = target / relative
                val finalDest = transformPath(dest, target, pathMapping)
                finalDest.parent?.let { FileSystem.SYSTEM.createDirectories(it) }
                FileSystem.SYSTEM.copy(src, finalDest)
                logger.create(finalDest)
            }
        }
    }

    private fun walkAndLog(source: Path, target: Path, pathMapping: Map<String, String>, skipPatterns: Set<String>) {
        FileSystem.SYSTEM.listRecursively(source).forEach { src ->
            val relative = src.relativeTo(source)
            if (shouldExclude(relative, skipPatterns)) return@forEach
            val metadata = FileSystem.SYSTEM.metadata(src)
            if (!metadata.isDirectory) {
                val dest = target / relative
                val finalDest = transformPath(dest, target, pathMapping)
                logger.create(finalDest)
            }
        }
    }

    fun replacePlaceholders(target: Path, mapping: Map<String, String>) {
        if (pretend) return
        FileSystem.SYSTEM.listRecursively(target).forEach { file ->
            val metadata = FileSystem.SYSTEM.metadata(file)
            if (!metadata.isRegularFile) return@forEach
            if (!isTextFile(file)) return@forEach
            var content = FileSystem.SYSTEM.read(file) { readUtf8() }
            var modified = false
            for ((placeholder, value) in mapping) {
                if (placeholder in content) {
                    content = content.replace(placeholder, value)
                    modified = true
                }
            }
            if (modified) {
                FileSystem.SYSTEM.write(file) { writeUtf8(content) }
                logger.update(file)
            }
        }
    }

    private fun isTextFile(path: Path): Boolean {
        val ext = path.name.substringAfterLast('.', "").lowercase()
        val binaryExtensions = setOf(
            "jar", "class", "png", "jpg", "jpeg", "gif", "bmp", "tiff", "tif", "webp",
            "ico", "mp3", "mp4", "mov", "avi", "pdf", "zip", "tar", "gz", "bz2",
            "xz", "7z", "rar", "keystore", "jks", "p12", "pem", "der", "DS_Store",
            "lock", "sum", "sha256", "sha1", "md5", "ttf", "otf", "woff",
            "woff2", "eot", "svgz", "swf", "exe", "dll", "so", "dylib", "a", "o"
        )
        if (ext in binaryExtensions) return false
        val bytes = FileSystem.SYSTEM.read(path) { readByteArray() }
        val sampleSize = minOf(bytes.size, 8192)
        for (i in 0 until sampleSize) {
            if (bytes[i].toInt() == 0) return false
        }
        return true
    }
}
