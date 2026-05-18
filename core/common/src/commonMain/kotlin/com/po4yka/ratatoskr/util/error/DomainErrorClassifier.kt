package com.po4yka.ratatoskr.util.error

/**
 * Coarse category for any [Throwable] caught at a ViewModel / repository
 * boundary. Used by ViewModels and the in-flight finish-toogenericexception
 * triage so call sites can pick a category from one source of truth
 * instead of each one re-classifying the same Ktor / SQLite / Cancellation
 * shapes inline.
 */
enum class DomainErrorCategory {
    Cancelled,
    Network,
    Database,
    Validation,
    Unknown,
}

/**
 * Pure classifier that maps a fully-qualified exception class name to a
 * [DomainErrorCategory].
 *
 * The atom operates on the class-name string rather than a [Throwable]
 * reference so it stays commonMain-safe (no need for `KClass` reflection,
 * no platform-specific exception imports) and so the caller can swap in
 * a fake class name in tests without instantiating the actual exception.
 *
 * Precedence (strongest first):
 *  1. `Cancellation` substring → [Cancelled]. CLAUDE.md requires
 *     rethrow as the first catch clause; this classifier mirrors that
 *     so a composite class name (e.g. `CancellableIOException`) still
 *     surfaces as cancellation rather than masking it as Network.
 *  2. Ktor network / IO / Socket substrings → [Network].
 *  3. SQL / SQLite / SQLDelight substrings → [Database].
 *  4. IllegalArgument / IllegalState substrings → [Validation].
 *  5. Anything else → [Unknown].
 *
 * Match is case-sensitive (class names always are). Empty / blank input
 * collapses to [Unknown].
 *
 * Pure, side-effect-free, deterministic.
 */
object DomainErrorClassifier {
    fun categoryFor(exceptionClassName: String): DomainErrorCategory {
        if (exceptionClassName.isBlank()) return DomainErrorCategory.Unknown
        return when {
            exceptionClassName.contains("Cancellation") -> DomainErrorCategory.Cancelled
            exceptionClassName.contains("CancellationException") -> DomainErrorCategory.Cancelled
            isNetwork(exceptionClassName) -> DomainErrorCategory.Network
            isDatabase(exceptionClassName) -> DomainErrorCategory.Database
            isValidation(exceptionClassName) -> DomainErrorCategory.Validation
            else -> DomainErrorCategory.Unknown
        }
    }

    private fun isNetwork(name: String): Boolean =
        name.contains("IOException") ||
            name.contains("SocketException") ||
            name.contains("SocketTimeout") ||
            name.contains("HttpRequestTimeout") ||
            name.contains("UnresolvedAddress") ||
            name.contains("ConnectException")

    private fun isDatabase(name: String): Boolean =
        name.contains("SQLException") ||
            name.contains("SQLite") ||
            name.contains("SqlDelight") ||
            name.contains("sqldelight")

    private fun isValidation(name: String): Boolean =
        name.contains("IllegalArgumentException") ||
            name.contains("IllegalStateException")
}
