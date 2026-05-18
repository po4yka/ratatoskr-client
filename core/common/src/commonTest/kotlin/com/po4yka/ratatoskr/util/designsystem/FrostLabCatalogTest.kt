package com.po4yka.ratatoskr.util.designsystem

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FrostLabCatalogTest {
    private val bracketButton =
        FrostLabSection(
            id = "bracket-button",
            displayName = "BracketButton",
            variants = listOf("default", "disabled", "focused"),
        )

    private val statusBadge =
        FrostLabSection(
            id = "status-badge",
            displayName = "StatusBadge",
            variants = listOf("info", "success", "error"),
        )

    @Test
    fun `valid catalog — Valid`() {
        assertEquals(
            FrostLabCatalog.ValidationResult.Valid,
            FrostLabCatalog.validate(listOf(bracketButton, statusBadge)),
        )
    }

    @Test
    fun `duplicate id — Invalid with error`() {
        // Two sections with the same id would collide in the Lab's
        // route param and in the screenshot-test golden path. Pin so
        // a copy-paste bug surfaces at catalog-validate time.
        val dup =
            FrostLabSection(
                id = "bracket-button",
                displayName = "AnotherButton",
                variants = listOf("default"),
            )
        val result = FrostLabCatalog.validate(listOf(bracketButton, dup))
        assertTrue(result is FrostLabCatalog.ValidationResult.Invalid)
        assertTrue(
            (result as FrostLabCatalog.ValidationResult.Invalid).errors.any { it.contains("duplicate id") },
            "expected a duplicate-id error, got ${result.errors}",
        )
    }

    @Test
    fun `blank id — Invalid`() {
        val blank =
            FrostLabSection(id = "   ", displayName = "Something", variants = listOf("default"))
        val result = FrostLabCatalog.validate(listOf(blank))
        assertTrue(result is FrostLabCatalog.ValidationResult.Invalid)
    }

    @Test
    fun `id must be kebab-case — uppercase rejected`() {
        // The id is used in route URLs / golden filenames; restrict to
        // lowercase ASCII + dash to keep both surfaces stable.
        val mixedCase =
            FrostLabSection(
                id = "BracketButton",
                displayName = "BracketButton",
                variants = listOf("default"),
            )
        val result = FrostLabCatalog.validate(listOf(mixedCase))
        assertTrue(result is FrostLabCatalog.ValidationResult.Invalid)
    }

    @Test
    fun `id must be kebab-case — underscore rejected`() {
        val underscored =
            FrostLabSection(
                id = "bracket_button",
                displayName = "BracketButton",
                variants = listOf("default"),
            )
        val result = FrostLabCatalog.validate(listOf(underscored))
        assertTrue(result is FrostLabCatalog.ValidationResult.Invalid)
    }

    @Test
    fun `blank displayName — Invalid`() {
        // Section header must render — a blank label reads as a
        // broken catalog row in the Lab.
        val noName =
            FrostLabSection(id = "x-atom", displayName = "  ", variants = listOf("default"))
        val result = FrostLabCatalog.validate(listOf(noName))
        assertTrue(result is FrostLabCatalog.ValidationResult.Invalid)
    }

    @Test
    fun `empty variants list — Invalid`() {
        // Every Frost atom in the Lab needs at least the default
        // variant, otherwise the section renders empty.
        val noVariants =
            FrostLabSection(id = "x-atom", displayName = "X", variants = emptyList())
        val result = FrostLabCatalog.validate(listOf(noVariants))
        assertTrue(result is FrostLabCatalog.ValidationResult.Invalid)
    }

    @Test
    fun `blank variant name — Invalid`() {
        val blankVariant =
            FrostLabSection(
                id = "x-atom",
                displayName = "X",
                variants = listOf("default", "   "),
            )
        val result = FrostLabCatalog.validate(listOf(blankVariant))
        assertTrue(result is FrostLabCatalog.ValidationResult.Invalid)
    }

    @Test
    fun `byId — returns matching section`() {
        assertEquals(
            bracketButton,
            FrostLabCatalog.byId(listOf(bracketButton, statusBadge), id = "bracket-button"),
        )
    }

    @Test
    fun `byId — unknown id returns null`() {
        assertNull(FrostLabCatalog.byId(listOf(bracketButton), id = "no-such-atom"))
    }

    @Test
    fun `validate is deterministic`() {
        val a = FrostLabCatalog.validate(listOf(bracketButton, statusBadge))
        val b = FrostLabCatalog.validate(listOf(bracketButton, statusBadge))
        assertEquals(a, b)
    }
}
