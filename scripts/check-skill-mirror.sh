#!/usr/bin/env bash
# Verify that .claude/skills and .codex/skills hold byte-identical mirrors.
#
# The repo ships agent-skill files in two parallel trees so that Claude Code
# (.claude/) and Codex (.codex/) both pick them up. They are intentionally
# duplicated rather than symlinked because some skill loaders don't traverse
# symlinks. This script is the only thing keeping them in sync.
#
# Run locally: ./scripts/check-skill-mirror.sh
# Wired into CI via .github/workflows/ci.yml.
set -euo pipefail

cd "$(dirname "$0")/.."

LEFT=".claude/skills"
RIGHT=".codex/skills"

if [ ! -d "$LEFT" ] || [ ! -d "$RIGHT" ]; then
  echo "error: expected both $LEFT and $RIGHT to exist" >&2
  exit 2
fi

if diff -rq "$LEFT" "$RIGHT" > /tmp/skill-mirror-diff.txt 2>&1; then
  echo "ok: $LEFT and $RIGHT are in sync"
  exit 0
fi

echo "error: $LEFT and $RIGHT have diverged" >&2
echo >&2
cat /tmp/skill-mirror-diff.txt >&2
echo >&2
echo "To resync from .claude -> .codex (most common direction):" >&2
echo "  rsync -a --delete .claude/skills/ .codex/skills/" >&2
echo >&2
echo "Or, if .codex is the freshly edited side:" >&2
echo "  rsync -a --delete .codex/skills/ .claude/skills/" >&2
exit 1
