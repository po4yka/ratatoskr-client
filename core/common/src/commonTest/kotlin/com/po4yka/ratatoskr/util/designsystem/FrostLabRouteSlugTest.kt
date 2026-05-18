package com.po4yka.ratatoskr.util.designsystem

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class FrostLabRouteSlugTest {
    @Test
    fun `section-only slug round-trips`() {
        val slug = FrostLabRouteSlug.build(sectionId = "bracket-button", variantId = null)
        assertEquals("bracket-button", slug)
        assertNotNull(slug)
        assertEquals(
            FrostLabSlug(sectionId = "bracket-button", variantId = null),
            FrostLabRouteSlug.parse(slug = slug),
        )
    }

    @Test
    fun `section-plus-variant slug round-trips`() {
        // Deep-link variant target so a screenshot test can jump
        // straight to "bracket-button focused" without scrolling.
        val slug = FrostLabRouteSlug.build(sectionId = "bracket-button", variantId = "focused")
        assertEquals("bracket-button::focused", slug)
        assertNotNull(slug)
        assertEquals(
            FrostLabSlug(sectionId = "bracket-button", variantId = "focused"),
            FrostLabRouteSlug.parse(slug = slug),
        )
    }

    @Test
    fun `build refuses non-kebab-case segments`() {
        // The slug feeds into the Decompose route param; restricting
        // to kebab-case keeps the route string stable and predictable.
        assertNull(FrostLabRouteSlug.build(sectionId = "BracketButton", variantId = null))
        assertNull(FrostLabRouteSlug.build(sectionId = "bracket-button", variantId = "Focused"))
        assertNull(FrostLabRouteSlug.build(sectionId = "bracket_button", variantId = null))
    }

    @Test
    fun `parse blank — null`() {
        assertNull(FrostLabRouteSlug.parse(slug = ""))
        assertNull(FrostLabRouteSlug.parse(slug = "   "))
    }

    @Test
    fun `parse rejects extra separator segments`() {
        // Three colon-pairs would be a malformed deep link. Refuse so
        // a corrupt URL never silently routes to a partial state.
        assertNull(FrostLabRouteSlug.parse(slug = "bracket-button::focused::extra"))
    }

    @Test
    fun `parse rejects non-kebab-case segments`() {
        assertNull(FrostLabRouteSlug.parse(slug = "BracketButton"))
        assertNull(FrostLabRouteSlug.parse(slug = "bracket-button::Focused"))
    }

    @Test
    fun `parse rejects blank section or variant after split`() {
        // `bracket-button::` reads as "section present, variant blank".
        // Refuse — the variant is meant to be either absent (no `::`)
        // or a valid kebab slug.
        assertNull(FrostLabRouteSlug.parse(slug = "bracket-button::"))
        assertNull(FrostLabRouteSlug.parse(slug = "::focused"))
    }

    @Test
    fun `build is deterministic`() {
        val a = FrostLabRouteSlug.build(sectionId = "bracket-button", variantId = "focused")
        val b = FrostLabRouteSlug.build(sectionId = "bracket-button", variantId = "focused")
        assertEquals(a, b)
    }
}
