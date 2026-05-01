#!/usr/bin/env bash
set -euo pipefail
violations=$(grep -rn 'androidx\.compose\.material3\.' \
    core/ui/src feature/auth/src feature/collections/src \
    feature/digest/src feature/settings/src feature/summary/src \
    composeApp/src 2>/dev/null \
    | grep -v 'material3\.Icon' || true)
if [ -n "$violations" ]; then
    echo "Material 3 widgets found outside of Icon — Frost forbids this:"
    echo "$violations"
    exit 1
fi
echo "check-no-m3: OK — only material3.Icon references remain."
