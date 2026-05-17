package com.po4yka.ratatoskr.util.plural

import kotlin.math.absoluteValue

/**
 * Classifies an integer count into the Russian CLDR cardinal plural
 * form so the upcoming `plurals.xml` migration (filed as
 * wire-no-hardcoded-compose-text-detekt-rule-and-migrate-literals) can
 * pick the right noun shape for counts like
 * "1 выделение / 2 выделения / 5 выделений".
 *
 * CLDR cardinal rules (cldr-core, releases/45):
 *  - one:  v == 0 && n % 10 == 1 && n % 100 != 11
 *  - few:  v == 0 && n % 10 in 2..4 && n % 100 !in 12..14
 *  - many: v == 0 && (n % 10 == 0 || n % 10 in 5..9 || n % 100 in 11..14)
 *  - other: covers fractional counts; integer-only entrypoint never
 *    emits other, so [Form] does not enumerate it.
 *
 * Defensive properties:
 *  - negative counts are normalized via [Int.absoluteValue] cast through
 *    [Long] so [Int.MIN_VALUE] does not overflow.
 *  - the result is one of three forms — the integer-only contract
 *    excludes the CLDR "other" form which only applies to decimals.
 *
 * Pure, side-effect-free, allocation-free hot path.
 */
object RussianPluralRule {
    enum class Form { ONE, FEW, MANY }

    fun select(count: Int): Form {
        val n = count.toLong().absoluteValue
        val lastTwo = (n % 100L).toInt()
        val lastDigit = (n % 10L).toInt()
        return when {
            lastTwo in 11..14 -> Form.MANY
            lastDigit == 1 -> Form.ONE
            lastDigit in 2..4 -> Form.FEW
            else -> Form.MANY
        }
    }
}
