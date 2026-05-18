package com.po4yka.ratatoskr.util.perf

/**
 * Single-character flags Android's baseline-profile format prefixes a
 * descriptor with:
 *  - [Hot]          — `H`, AOT-compile because the symbol is hot at runtime.
 *  - [Startup]      — `S`, AOT-compile because the symbol is on the cold-start path.
 *  - [PostStartup]  — `P`, AOT-compile because the symbol is on the post-startup path.
 * Flags can co-occur in any subset; multiple flags share the prefix
 * (e.g. `HSPLcom/foo/Bar;`).
 */
enum class RuleFlag {
    Hot,
    Startup,
    PostStartup,
}

/**
 * Outcome of parsing a single line from a baseline-profile file.
 * A real profile mixes [Rule] entries with [Comment] and [Blank] padding;
 * [Invalid] surfaces parse failures so the caller can fail-loud rather
 * than silently dropping a malformed line.
 */
sealed interface BaselineProfileLine {
    data class Rule(
        val flags: Set<RuleFlag>,
        val classDescriptor: String,
        val member: String?,
    ) : BaselineProfileLine

    data object Comment : BaselineProfileLine

    data object Blank : BaselineProfileLine

    data object Invalid : BaselineProfileLine
}

/**
 * Pure parser for one line of an Android baseline-profile file. The
 * androidApp:benchmark module's profile generator will produce these
 * lines, and a future CI drift check can fold lines through this atom
 * to detect malformed entries before they reach profileinstaller.
 *
 * Grammar pinned:
 *  - `#…`                                 → Comment
 *  - empty / whitespace-only              → Blank
 *  - `[HSP]*Lpkg/Class;`                  → Rule (class-only)
 *  - `[HSP]*Lpkg/Class;->method(sig)R`    → Rule (member)
 *  - anything else                        → Invalid
 *
 * Pure, side-effect-free, deterministic.
 */
object BaselineProfileEntry {
    private const val DESCRIPTOR_OPEN = 'L'
    private const val DESCRIPTOR_CLOSE = ';'
    private const val METHOD_ARROW = "->"
    private const val COMMENT_PREFIX = "#"

    fun parse(line: String): BaselineProfileLine {
        val trimmed = line.trim()
        if (trimmed.isEmpty()) return BaselineProfileLine.Blank
        if (trimmed.startsWith(COMMENT_PREFIX)) return BaselineProfileLine.Comment
        val (flags, afterFlags) = consumeFlags(trimmed)
        if (afterFlags.isEmpty() || afterFlags[0] != DESCRIPTOR_OPEN) return BaselineProfileLine.Invalid
        val semicolonIndex = afterFlags.indexOf(DESCRIPTOR_CLOSE)
        if (semicolonIndex < 0) return BaselineProfileLine.Invalid
        val classDescriptor = afterFlags.substring(0, semicolonIndex + 1)
        val tail = afterFlags.substring(semicolonIndex + 1)
        val member =
            when {
                tail.isEmpty() -> null
                tail.startsWith(METHOD_ARROW) -> tail.substring(METHOD_ARROW.length)
                else -> return BaselineProfileLine.Invalid
            }
        return BaselineProfileLine.Rule(
            flags = flags,
            classDescriptor = classDescriptor,
            member = member,
        )
    }

    private fun consumeFlags(input: String): Pair<Set<RuleFlag>, String> {
        val flags = mutableSetOf<RuleFlag>()
        var i = 0
        while (i < input.length) {
            val flag =
                when (input[i]) {
                    'H' -> RuleFlag.Hot
                    'S' -> RuleFlag.Startup
                    'P' -> RuleFlag.PostStartup
                    else -> null
                } ?: break
            flags.add(flag)
            i++
        }
        return flags to input.substring(i)
    }
}
