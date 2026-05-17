package com.po4yka.ratatoskr.util.plural

import kotlin.test.Test
import kotlin.test.assertEquals

class RussianPluralRuleTest {
    @Test
    fun `ONE — count ending in 1 except for the teen-11 exception`() {
        // CLDR rule "one": n % 10 == 1 && n % 100 != 11.
        // 1 highlight, 21 highlights, 101 highlights all read with the
        // same Russian noun form ("выделение").
        assertEquals(RussianPluralRule.Form.ONE, RussianPluralRule.select(1))
        assertEquals(RussianPluralRule.Form.ONE, RussianPluralRule.select(21))
        assertEquals(RussianPluralRule.Form.ONE, RussianPluralRule.select(101))
        assertEquals(RussianPluralRule.Form.ONE, RussianPluralRule.select(1001))
    }

    @Test
    fun `FEW — counts ending in 2-4 except for the teen-12-14 exception`() {
        // CLDR rule "few": n % 10 in 2..4 && (n % 100 < 12 || n % 100 > 14).
        // 2 highlights ("выделения"), 22, 23, 24 use the same form.
        assertEquals(RussianPluralRule.Form.FEW, RussianPluralRule.select(2))
        assertEquals(RussianPluralRule.Form.FEW, RussianPluralRule.select(3))
        assertEquals(RussianPluralRule.Form.FEW, RussianPluralRule.select(4))
        assertEquals(RussianPluralRule.Form.FEW, RussianPluralRule.select(22))
        assertEquals(RussianPluralRule.Form.FEW, RussianPluralRule.select(23))
        assertEquals(RussianPluralRule.Form.FEW, RussianPluralRule.select(24))
        assertEquals(RussianPluralRule.Form.FEW, RussianPluralRule.select(102))
        assertEquals(RussianPluralRule.Form.FEW, RussianPluralRule.select(104))
    }

    @Test
    fun `MANY — counts ending in 0 or 5-9`() {
        // CLDR rule "many" includes: n % 10 == 0 || n % 10 in 5..9.
        // 0, 5, 6, 7, 8, 9, 10, 20, 25, 100 all use "выделений".
        assertEquals(RussianPluralRule.Form.MANY, RussianPluralRule.select(0))
        assertEquals(RussianPluralRule.Form.MANY, RussianPluralRule.select(5))
        assertEquals(RussianPluralRule.Form.MANY, RussianPluralRule.select(9))
        assertEquals(RussianPluralRule.Form.MANY, RussianPluralRule.select(10))
        assertEquals(RussianPluralRule.Form.MANY, RussianPluralRule.select(20))
        assertEquals(RussianPluralRule.Form.MANY, RussianPluralRule.select(25))
        assertEquals(RussianPluralRule.Form.MANY, RussianPluralRule.select(100))
    }

    @Test
    fun `MANY — teen exception covers 11 through 19`() {
        // The 11–14 group is the classic Russian-plural gotcha: even
        // though 11 ends in 1 and 12-14 end in 2-4, all teens use MANY.
        // This is exactly the bug that ad-hoc `if count == 1 else plural`
        // pluralization shipped to RU-locale users.
        for (n in 11..19) {
            assertEquals(RussianPluralRule.Form.MANY, RussianPluralRule.select(n), "n=$n should be MANY")
        }
    }

    @Test
    fun `MANY — three-digit teens 111, 112, 114, 119`() {
        // Same teen rule applies at any hundreds offset: 111, 112, 113,
        // 114 are MANY because the last two digits land in 11–14.
        assertEquals(RussianPluralRule.Form.MANY, RussianPluralRule.select(111))
        assertEquals(RussianPluralRule.Form.MANY, RussianPluralRule.select(112))
        assertEquals(RussianPluralRule.Form.MANY, RussianPluralRule.select(114))
        assertEquals(RussianPluralRule.Form.MANY, RussianPluralRule.select(119))
    }

    @Test
    fun `negative counts are normalized to their absolute value`() {
        // CLDR is mute on negatives; the app treats counts as cardinal so
        // -1 reads identically to 1. Pin this so a future refactor that
        // introduces signed deltas doesn't silently change plural shape.
        assertEquals(RussianPluralRule.Form.ONE, RussianPluralRule.select(-1))
        assertEquals(RussianPluralRule.Form.FEW, RussianPluralRule.select(-3))
        assertEquals(RussianPluralRule.Form.MANY, RussianPluralRule.select(-11))
    }

    @Test
    fun `Int MIN_VALUE does not overflow when negated`() {
        // Math.absoluteValue throws / returns Int.MIN_VALUE for the
        // smallest int. The implementation must defend against this — the
        // app never realistically passes such a value, but a safe fallback
        // is cheaper than a hard-to-trace crash on a corrupt deserialized
        // count.
        val result = RussianPluralRule.select(Int.MIN_VALUE)
        // MIN_VALUE = -2147483648; abs would overflow but |MIN_VALUE| % 10
        // is 8 in the modular sense, so should fall into MANY.
        assertEquals(RussianPluralRule.Form.MANY, result)
    }
}
