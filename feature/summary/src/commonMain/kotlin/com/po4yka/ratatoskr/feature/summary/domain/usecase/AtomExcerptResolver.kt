package com.po4yka.ratatoskr.feature.summary.domain.usecase

/**
 * Locates the article excerpt that backs an `InsightFact` (atom). The backend
 * sometimes provides an offset directly — when it does the caller skips this
 * resolver. When it doesn't, this fuzzy-match flow runs and returns the best
 * matching span so the atom drill-down dialog can quote it and scroll-to-source.
 *
 * Three strategies, tried in descending priority so the "scroll to source"
 * lands on the most-faithful excerpt:
 *
 *  1. EXACT — case-sensitive substring match. The atom appears verbatim.
 *  2. CASE_INSENSITIVE — lowercased substring match. Catches casing drift
 *     between the LLM-generated newFact and the article copy.
 *  3. FUZZY_SENTENCE — pick the article sentence with the highest fact-token
 *     recall (intersection ÷ fact-tokens-of-length>=4). Returns null below the
 *     [FUZZY_MIN_RECALL] threshold so an unrelated atom doesn't jump to a
 *     random sentence; the UI then renders no clickable affordance.
 *
 * Offsets index into the *original* fullContent unchanged, because the UI uses
 * them to scroll-to-source. Normalizing the source would drift the offsets.
 */
object AtomExcerptResolver {
    const val FUZZY_MIN_RECALL: Double = 0.33
    private const val MIN_TOKEN_LENGTH: Int = 4

    enum class MatchStrategy { EXACT, CASE_INSENSITIVE, FUZZY_SENTENCE }

    data class Match(val startOffset: Int, val endOffset: Int, val strategy: MatchStrategy)

    fun resolve(
        factText: String,
        fullContent: String?,
    ): Match? {
        val fact = factText.trim()
        if (fact.isEmpty()) return null
        val source = fullContent?.takeIf { it.isNotBlank() } ?: return null

        exactMatch(fact, source)?.let { return it }
        caseInsensitiveMatch(fact, source)?.let { return it }
        return fuzzySentenceMatch(fact, source)
    }

    private fun exactMatch(
        fact: String,
        source: String,
    ): Match? {
        val idx = source.indexOf(fact)
        return if (idx >= 0) Match(idx, idx + fact.length, MatchStrategy.EXACT) else null
    }

    private fun caseInsensitiveMatch(
        fact: String,
        source: String,
    ): Match? {
        val idx = source.indexOf(fact, ignoreCase = true)
        return if (idx >= 0) Match(idx, idx + fact.length, MatchStrategy.CASE_INSENSITIVE) else null
    }

    private fun fuzzySentenceMatch(
        fact: String,
        source: String,
    ): Match? {
        val factTokens = tokenize(fact)
        if (factTokens.isEmpty()) return null

        val sentences = splitSentences(source)
        var best: Match? = null
        var bestRecall = 0.0
        for (range in sentences) {
            val snippet = source.substring(range.first, range.last + 1)
            val sentenceTokens = tokenize(snippet)
            if (sentenceTokens.isEmpty()) continue
            val recall = sentenceTokens.intersect(factTokens).size.toDouble() / factTokens.size
            if (recall > bestRecall) {
                bestRecall = recall
                val trimmedStart = range.first + snippet.takeWhile { it.isWhitespace() }.length
                best = Match(trimmedStart, range.last + 1, MatchStrategy.FUZZY_SENTENCE)
            }
        }
        return if (bestRecall >= FUZZY_MIN_RECALL) best else null
    }

    private fun tokenize(text: String): Set<String> =
        text.lowercase()
            .split(Regex("[^a-z0-9']+"))
            .filter { it.length >= MIN_TOKEN_LENGTH }
            .toSet()

    private fun splitSentences(source: String): List<IntRange> {
        val result = mutableListOf<IntRange>()
        var start = 0
        for (i in source.indices) {
            val c = source[i]
            if (c == '.' || c == '!' || c == '?' || c == '\n') {
                if (i > start) result.add(start..i)
                start = i + 1
            }
        }
        if (start < source.length) result.add(start until source.length)
        return result
    }
}
