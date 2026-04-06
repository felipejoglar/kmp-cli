plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

version = findProperty("cliVersion")?.toString() ?: "0.0.1"

repositories {
    mavenCentral()
}

kotlin {
    val targets = listOf(
        macosArm64(),
        linuxX64(),
        linuxArm64(),
    )

    targets.forEach { target ->
        target.binaries.executable {
            entryPoint = "kmp.cli.main"
            baseName = "kmp-cli"
        }
    }

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.clikt)
            implementation(libs.okio)
        }
    }
}

val generateVersionFile by tasks.registering {
    val outputDir = layout.buildDirectory.dir("generated/version")
    val version = project.version.toString()
    outputs.dir(outputDir)
    inputs.property("version", version)
    doLast {
        val file = outputDir.get().file("kmp/cli/BuildConfig.kt").asFile
        file.parentFile.mkdirs()
        file.writeText(
            """
            |package kmp.cli
            |
            |object BuildConfig {
            |    const val VERSION = "$version"
            |}
            """.trimMargin()
        )
    }
}

kotlin.sourceSets.commonMain { kotlin.srcDir(generateVersionFile) }

val assembleDist by tasks.registering(Sync::class) {
    val hostTarget = when {
        org.gradle.internal.os.OperatingSystem.current().isMacOsX -> "macosArm64"
        org.gradle.internal.os.OperatingSystem.current().isLinux && System.getProperty("os.arch") == "aarch64" -> "linuxArm64"
        org.gradle.internal.os.OperatingSystem.current().isLinux -> "linuxX64"
        else -> error("Unsupported OS")
    }
    val linkTask = tasks.named("linkReleaseExecutable${hostTarget.replaceFirstChar { it.uppercase() }}")
    dependsOn(linkTask)

    val distDir = layout.buildDirectory.dir("dist")
    into(distDir)

    from(layout.buildDirectory.dir("bin/$hostTarget/releaseExecutable")) {
        into("bin")
    }
    from("src/main/resources/templates") {
        into("bin/templates")
    }
}

tasks.named<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
