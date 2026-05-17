package com.po4yka.ratatoskr.util.lint

/**
 * Pure predicate that powers the upcoming `NoHardcodedComposeText` detekt rule.
 * Given a string literal used as an argument to a Compose call site
 * (`Text("…")`, `contentDescription = "…"`, `BracketButton(label = "…")`),
 * it answers: is this likely a user-facing label that should have come from
 * `stringResource(Res.string.…)`?
 *
 * The heuristic intentionally errs on the side of letting test-tag and
 * resource-key literals pass — the false positive cost (forcing a developer
 * to rewrite `Modifier.testTag("summary_list_root")` as a string resource) is
 * higher than the false negative cost (one missed migration that another rule
 * pass or PR review can still catch).
 *
 * Order of rules:
 *  1. Length-1 trimmed literals do not flag (separators, mnemonics, "x").
 *  2. Any whitespace → user-facing (prose, multi-word labels, formats).
 *  3. Any code-like separator (`_`, `-`, `.`, `/`, `:`, `\`) → not flagged.
 *  4. First non-whitespace char must be an uppercase letter → flagged.
 *
 * Allowlisting (e.g. Frost Lab demo screens where literal text is intentional)
 * is a separate concern handled by the detekt rule using the call site's file
 * path; this predicate operates only on the literal value itself.
 */
object ComposeLiteralTextHeuristic {
    private val CODE_LIKE_SEPARATORS = charArrayOf('_', '-', '.', '/', ':', '\\')

    fun looksLikeUserText(literal: String): Boolean {
        val trimmed = literal.trim()
        if (trimmed.length < 2) return false
        if (trimmed.any { it.isWhitespace() }) return true
        if (trimmed.any { it in CODE_LIKE_SEPARATORS }) return false
        val first = trimmed.first()
        if (!first.isLetter()) return false
        return first.isUpperCase()
    }
}
