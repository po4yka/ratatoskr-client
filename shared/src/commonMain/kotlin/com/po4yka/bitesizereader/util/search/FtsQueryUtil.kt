package com.po4yka.bitesizereader.util.search

/**
 * Utility for FTS4 query sanitization.
 * FTS4 MATCH clauses interpret special characters as operators.
 * This utility escapes user input to prevent FTS4 injection.
 *
 * FTS4 Special operators that need escaping:
 * - AND, OR, NOT (boolean operators)
 * - NEAR (proximity operator)
 * - * (prefix matching)
 * - - (exclude terms)
 * - : (column specifier)
 * - " (phrase delimiter)
 * - ( ) (grouping)
 */
object FtsQueryUtil {
    /**
     * Escapes user input for safe use in FTS4 MATCH queries.
     * Wraps the query in double quotes to treat it as a phrase search,
     * and escapes any embedded double quotes.
     *
     * Example:
     * - Input: `hello world` -> Output: `"hello world"`
     * - Input: `test "quoted"` -> Output: `"test \"quoted\""`
     * - Input: `AND OR` -> Output: `"AND OR"` (treated as literal text)
     *
     * @param query User input search query
     * @return Escaped query safe for FTS4 MATCH
     */
    fun escapeFts4Query(query: String): String {
        if (query.isBlank()) return query

        // Escape double quotes by doubling them (standard SQL escaping)
        val escaped = query.replace("\"", "\"\"")

        // Wrap in quotes to treat as phrase search, preventing operator interpretation
        return "\"$escaped\""
    }

    /**
     * Alternative escaping that preserves word boundaries for multi-word search.
     * Each word is escaped and combined with AND operator.
     *
     * Example:
     * - Input: `hello world` -> Output: `"hello" "world"`
     *
     * @param query User input search query
     * @return Escaped query with words as separate phrases
     */
    fun escapeFts4QueryWords(query: String): String {
        if (query.isBlank()) return query

        return query.split(Regex("\\s+"))
            .filter { it.isNotBlank() }
            .joinToString(" ") { word ->
                "\"${word.replace("\"", "\"\"")}\""
            }
    }
}
