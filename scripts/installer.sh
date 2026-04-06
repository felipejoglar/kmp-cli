#!/bin/bash
set -e

# KMP CLI installer
# Downloads the latest release from GitHub and installs the binary to ~/bin

REPO="felipejoglar/kmp-cli"
INSTALL_DIR="$HOME/bin"
BINARY_NAME="kmp-cli"

# Detect OS and architecture
OS=$(uname -s | tr '[:upper:]' '[:lower:]')
ARCH=$(uname -m)

case "$OS" in
    darwin)
        # Apple Silicon only; Intel Macs are no longer supported
        ARCH="arm64" ;;
    linux)
        case "$ARCH" in
            x86_64)  ARCH="amd64" ;;
            aarch64) ARCH="arm64" ;;
            arm64)   ARCH="arm64" ;;
            *) echo "Unsupported architecture: $ARCH"; exit 1 ;;
        esac ;;
    *) echo "Unsupported OS: $OS"; exit 1 ;;
esac

# Detect latest version by following the releases/latest redirect (no API rate limits)
VERSION=$(curl -fsSI "https://github.com/$REPO/releases/latest" | grep -i '^location:' | sed -E 's|.*/v?([0-9][^ /\r]*).*|\1|')

if [ -z "$VERSION" ]; then
    echo "Error: Could not determine the latest release version."
    echo "Check https://github.com/$REPO/releases for available releases."
    exit 1
fi

ARCHIVE="${BINARY_NAME}-${OS}-${ARCH}.tar.gz"
DOWNLOAD_URL="https://github.com/$REPO/releases/download/v$VERSION/$ARCHIVE"
CHECKSUMS_URL="https://github.com/$REPO/releases/download/v$VERSION/checksums.txt"

echo "Downloading KMP CLI v$VERSION for $OS/$ARCH..."
curl -fsSL "$DOWNLOAD_URL" -o "/tmp/$ARCHIVE"
curl -fsSL "$CHECKSUMS_URL" -o "/tmp/checksums.txt"

# Verify checksum
echo "Verifying checksum..."
EXPECTED=$(grep "$ARCHIVE" "/tmp/checksums.txt" | awk '{print $1}')
if [ -z "$EXPECTED" ]; then
    echo "Error: Archive not found in checksums.txt"
    rm -f "/tmp/$ARCHIVE" "/tmp/checksums.txt"
    exit 1
fi

if command -v sha256sum >/dev/null 2>&1; then
    ACTUAL=$(sha256sum "/tmp/$ARCHIVE" | awk '{print $1}')
elif command -v shasum >/dev/null 2>&1; then
    ACTUAL=$(shasum -a 256 "/tmp/$ARCHIVE" | awk '{print $1}')
else
    echo "Warning: No sha256sum or shasum available, skipping verification"
    ACTUAL="$EXPECTED"
fi

if [ "$EXPECTED" != "$ACTUAL" ]; then
    echo "Error: Checksum verification failed!"
    echo "  Expected: $EXPECTED"
    echo "  Actual:   $ACTUAL"
    rm -f "/tmp/$ARCHIVE" "/tmp/checksums.txt"
    exit 1
fi

# Extract and install
mkdir -p "$INSTALL_DIR"
echo "Installing to $INSTALL_DIR..."
tar -xzf "/tmp/$ARCHIVE" -C "$INSTALL_DIR" --strip-components=1

# Cleanup
rm -f "/tmp/$ARCHIVE" "/tmp/checksums.txt"

echo "KMP CLI installed successfully!"

# Check if ~/bin is in PATH
if [[ ":$PATH:" != *":$INSTALL_DIR:"* ]]; then
    echo ""
    echo "Note: $INSTALL_DIR is not in your PATH."
    echo "Add it by running:"
    echo "  echo 'export PATH=\"\$HOME/bin:\$PATH\"' >> ~/.zshrc && source ~/.zshrc"
fi

echo "Run 'kmp-cli new MyApp' to create a new project."
