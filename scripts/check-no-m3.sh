#!/usr/bin/env bash
# Backwards-compatible shim. The canonical Frost "no Material 3" check is now
# a Gradle task wired into `./gradlew check`. Kept here so contributors with
# muscle memory still get the right behaviour locally.
set -euo pipefail
cd "$(dirname "$0")/.."
exec ./gradlew --quiet verifyNoMaterial3
