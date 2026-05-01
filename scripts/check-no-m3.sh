#!/usr/bin/env bash
# Verifies that no shared Kotlin source imports androidx.compose.material3.*.
# Frost forbids Material 3 in commonMain, core/ui, and feature modules.
set -euo pipefail
violations=$(rg -g '*.kt' 'androidx\.compose\.material3\.' core/ui/src feature composeApp/src 2>/dev/null || true)
if [ -n "$violations" ]; then
    echo "Material 3 imports found — Frost forbids any androidx.compose.material3.* usage:"
    echo "$violations"
    exit 1
fi
echo "check-no-m3: OK — zero Material 3 imports."
