package com.po4yka.ratatoskr.util.lint

/**
 * Path-based allowlist predicate for the upcoming `NoHardcodedComposeText`
 * detekt rule. Companion to [ComposeLiteralTextHeuristic] — the heuristic
 * decides whether a literal *looks* user-facing; this predicate decides
 * whether the *file* is exempt from the rule entirely.
 *
 * Two allowlist shapes:
 *  1. Frost Lab demo screens (`FrostLab*Screen.kt` under
 *     `shared/sharedUI/.../ui/frost/`). The Lab is an in-app component browser
 *     of canonical Frost atoms — its literals are intentional dev fixtures.
 *  2. Any file in a test source set (`/src/commonTest/`,
 *     `/src/androidTest/`, `/src/iosTest/`, `/src/jvmTest/`, etc.) or any
 *     filename ending in `Test.kt`. Tests routinely construct
 *     `Text("Hello")` in fixture composables; flagging those would be
 *     pure noise.
 *
 * Path normalization handles Windows-style backslash separators so a CI
 * run on Windows produces the same verdict as local Mac/Linux.
 *
 * Pure, side-effect-free, deterministic.
 */
object ComposeTextAllowlist {
    private val FROST_LAB_REGEX = Regex("""/ui/frost/FrostLab[A-Za-z0-9]*Screen\.kt$""")
    private val TEST_SOURCE_SET_REGEX = Regex("""/src/[a-zA-Z]+Test/""")
    private const val TEST_FILE_SUFFIX = "Test.kt"

    fun isAllowlisted(filePath: String): Boolean {
        if (filePath.isEmpty()) return false
        val normalized = filePath.replace(oldChar = '\\', newChar = '/')
        if (FROST_LAB_REGEX.containsMatchIn(normalized)) return true
        if (TEST_SOURCE_SET_REGEX.containsMatchIn(normalized)) return true
        return normalized.endsWith(TEST_FILE_SUFFIX)
    }
}
