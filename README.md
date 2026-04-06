# kmp-cli

[![CI](https://github.com/felipejoglar/kmp-cli/actions/workflows/ci.yml/badge.svg)](https://github.com/felipejoglar/kmp-cli/actions/workflows/ci.yml)
[![Template Builds](https://github.com/felipejoglar/kmp-cli/actions/workflows/template-builds.yml/badge.svg)](https://github.com/felipejoglar/kmp-cli/actions/workflows/template-builds.yml)

A command-line tool to generate Kotlin Multiplatform projects similar to the kmp-template.

## Installation

The installer downloads the latest release from GitHub and installs the binary to `~/bin`. Add that directory to your `PATH` if not already included:
```bash
curl -fsSL https://raw.githubusercontent.com/felipejoglar/kmp-cli/main/scripts/installer.sh | bash
```

## Usage

Create a new KMP project:
```bash
kmp-cli new MyApp --package com.example.myapp
```

Options:
```
  -G, --skip-git     Skip git init
      --skip-deps    Skip running gradle assemble after generation
      --skip-ios     Skip iOS project generation
      --package=     Base package name (e.g., com.example) [default: com.example]
  -f, --force        Overwrite files that already exist
  -p, --pretend      Run but do not make any changes
  -q, --quiet        Suppress status output

  -v, --version      Show version number
```

## iOS Generation

The generated iOS project includes a pre-generated `.xcodeproj` with environment-based build configurations (Dev, Beta, Prod) that works out of the box.

To skip iOS generation entirely, use `--skip-ios`.

## Generated Structure

The generated project includes:
- **Root Gradle project** with version catalog
- **Shared KMP module** (`<modulename>/`) with Compose Multiplatform
- **Android app module** (`apps/android/`) with product flavors
- **iOS app** (`apps/ios/<projectname>/`) with XcodeGen project and environment configurations (when iOS is not skipped)

## Requirements

- JDK 17+
- Xcode (for iOS development)
- Android Studio (for Android development)

## License

```
MIT License

Copyright (c) 2026

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
