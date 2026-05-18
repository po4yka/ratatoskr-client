package com.po4yka.ratatoskr.util.typography

/**
 * Coarse-grained font-scale bucket. Used by the sp-literal migration so
 * Compose chrome (tab labels, status strips, sticky headers) can switch
 * to layout variants without inspecting the raw scale value at every
 * call site.
 *
 * The buckets aren't a perfect mirror of the OS slider; they're chosen
 * so the design system has predictable jump points:
 *  - [Compact]    — < 0.9  (rare; user shrank text)
 *  - [Default]    — 0.9..< 1.15 (the unaltered OS default)
 *  - [Large]      — 1.15..1.3 (slight enlargement; still single-line)
 *  - [ExtraLarge] — > 1.3 (accessibility-large; chrome tightens or wraps)
 */
enum class SpScaleBucket {
    Compact,
    Default,
    Large,
    ExtraLarge,
    ;

    companion object {
        fun bucketFor(fontScale: Float): SpScaleBucket =
            when {
                fontScale < 0.9f -> Compact
                fontScale < 1.15f -> Default
                fontScale <= 1.3f -> Large
                else -> ExtraLarge
            }
    }
}
