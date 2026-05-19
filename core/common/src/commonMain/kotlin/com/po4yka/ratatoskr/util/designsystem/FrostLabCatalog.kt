package com.po4yka.ratatoskr.util.designsystem

/**
 * One row in the Frost Lab catalog: a Frost atom name (the display
 * label that lives at the top of the section in the Lab UI) keyed by a
 * stable kebab-case id, plus the list of variant labels the section
 * lets the user toggle (e.g. `disabled`, `error`, `focused`).
 *
 * Kept as a pure data class — the actual Compose composable bindings
 * live in `shared/sharedUI` so commonMain stays free of UI imports.
 */
data class FrostLabSection(
    val id: String,
    val displayName: String,
    val variants: List<String>,
)

/**
 * Pure validator + lookup over a list of [FrostLabSection] rows. Used
 * both by the upcoming Frost Lab screen and by the Roborazzi snapshot
 * suite so a single registry drives both. Section ids feed into route
 * URLs and golden filenames, so the validator enforces a stable
 * `[a-z0-9-]+` shape.
 *
 * Rules:
 *  - id must be non-blank, kebab-case (`[a-z0-9-]+`).
 *  - ids must be unique across the whole list.
 *  - displayName must be non-blank.
 *  - variants must be non-empty; each variant name must be non-blank.
 *
 * Pure, side-effect-free, deterministic.
 */
object FrostLabCatalog {
    private val KEBAB_REGEX = Regex("^[a-z0-9]+(-[a-z0-9]+)*$")

    sealed interface ValidationResult {
        data object Valid : ValidationResult

        data class Invalid(val errors: List<String>) : ValidationResult
    }

    fun validate(sections: List<FrostLabSection>): ValidationResult {
        val errors = mutableListOf<String>()
        val seenIds = mutableSetOf<String>()
        sections.forEach { section ->
            val id = section.id.trim()
            when {
                id.isEmpty() -> errors += "blank id for section '${section.displayName}'"
                !KEBAB_REGEX.matches(id) -> errors += "id '$id' is not kebab-case"
                id in seenIds -> errors += "duplicate id '$id'"
                else -> seenIds += id
            }
            if (section.displayName.isBlank()) errors += "blank displayName for id '$id'"
            if (section.variants.isEmpty()) errors += "no variants for id '$id'"
            section.variants.forEachIndexed { index, variant ->
                if (variant.isBlank()) errors += "blank variant at index $index for id '$id'"
            }
        }
        return if (errors.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(errors)
    }

    fun byId(
        sections: List<FrostLabSection>,
        id: String,
    ): FrostLabSection? = sections.firstOrNull { it.id == id }
}
