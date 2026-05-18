package com.po4yka.ratatoskr.util.designsystem

/**
 * Parsed route slug for the Frost Lab. Either points at a section
 * (variant left null — render the section's default variant) or at a
 * specific variant inside a section.
 */
data class FrostLabSlug(
    val sectionId: String,
    val variantId: String?,
)

/**
 * Pure builder + parser for the Lab's Decompose route slug. Lets a
 * deep-link (or a screenshot test) jump straight to a section or to a
 * specific variant inside a section without scrolling.
 *
 * Format:
 *  - `<section>`               — section default variant
 *  - `<section>::<variant>`    — explicit variant inside a section
 *
 * Each segment must be kebab-case ASCII (`[a-z0-9]+(-[a-z0-9]+)*`).
 * Anything else fails the build/parse pair and the caller can treat
 * it as a corrupt URL rather than silently routing to a partial state.
 *
 * Pure, side-effect-free, deterministic.
 */
object FrostLabRouteSlug {
    const val VARIANT_SEPARATOR: String = "::"
    private val KEBAB_REGEX = Regex("^[a-z0-9]+(-[a-z0-9]+)*$")

    fun build(
        sectionId: String,
        variantId: String?,
    ): String? {
        if (!KEBAB_REGEX.matches(sectionId)) return null
        if (variantId == null) return sectionId
        if (!KEBAB_REGEX.matches(variantId)) return null
        return sectionId + VARIANT_SEPARATOR + variantId
    }

    fun parse(slug: String): FrostLabSlug? {
        val trimmed = slug.trim()
        if (trimmed.isEmpty()) return null
        val parts = trimmed.split(VARIANT_SEPARATOR)
        return when (parts.size) {
            1 -> if (KEBAB_REGEX.matches(parts[0])) FrostLabSlug(sectionId = parts[0], variantId = null) else null
            2 -> parseTwoPart(section = parts[0], variant = parts[1])
            else -> null
        }
    }

    private fun parseTwoPart(
        section: String,
        variant: String,
    ): FrostLabSlug? {
        if (!KEBAB_REGEX.matches(section)) return null
        if (!KEBAB_REGEX.matches(variant)) return null
        return FrostLabSlug(sectionId = section, variantId = variant)
    }
}
