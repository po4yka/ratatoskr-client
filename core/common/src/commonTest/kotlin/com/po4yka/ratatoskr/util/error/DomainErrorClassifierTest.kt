package com.po4yka.ratatoskr.util.error

import kotlin.test.Test
import kotlin.test.assertEquals

class DomainErrorClassifierTest {
    @Test
    fun `CancellationException — Cancelled — never user-visible`() {
        // Pin the canonical cancellation contract: CLAUDE.md requires
        // CancellationException rethrow as the first catch clause. The
        // classifier matches it so callers can short-circuit before
        // mapping to a user-facing message.
        assertEquals(
            DomainErrorCategory.Cancelled,
            DomainErrorClassifier.categoryFor("kotlin.coroutines.cancellation.CancellationException"),
        )
        assertEquals(
            DomainErrorCategory.Cancelled,
            DomainErrorClassifier.categoryFor("kotlinx.coroutines.JobCancellationException"),
        )
    }

    @Test
    fun `Ktor network exceptions — Network`() {
        assertEquals(
            DomainErrorCategory.Network,
            DomainErrorClassifier.categoryFor("io.ktor.client.network.sockets.SocketTimeoutException"),
        )
        assertEquals(
            DomainErrorCategory.Network,
            DomainErrorClassifier.categoryFor("io.ktor.client.plugins.HttpRequestTimeoutException"),
        )
        assertEquals(
            DomainErrorCategory.Network,
            DomainErrorClassifier.categoryFor("io.ktor.client.network.UnresolvedAddressException"),
        )
    }

    @Test
    fun `IOException family — Network`() {
        assertEquals(
            DomainErrorCategory.Network,
            DomainErrorClassifier.categoryFor("java.io.IOException"),
        )
        assertEquals(
            DomainErrorCategory.Network,
            DomainErrorClassifier.categoryFor("java.net.SocketException"),
        )
    }

    @Test
    fun `SQLException family — Database`() {
        assertEquals(
            DomainErrorCategory.Database,
            DomainErrorClassifier.categoryFor("java.sql.SQLException"),
        )
        assertEquals(
            DomainErrorCategory.Database,
            DomainErrorClassifier.categoryFor("android.database.sqlite.SQLiteConstraintException"),
        )
        assertEquals(
            DomainErrorCategory.Database,
            DomainErrorClassifier.categoryFor("app.cash.sqldelight.db.QueryResult"),
        )
    }

    @Test
    fun `IllegalArgumentException — Validation`() {
        assertEquals(
            DomainErrorCategory.Validation,
            DomainErrorClassifier.categoryFor("java.lang.IllegalArgumentException"),
        )
        assertEquals(
            DomainErrorCategory.Validation,
            DomainErrorClassifier.categoryFor("kotlin.IllegalStateException"),
        )
    }

    @Test
    fun `unknown exception class — Unknown`() {
        assertEquals(
            DomainErrorCategory.Unknown,
            DomainErrorClassifier.categoryFor("com.example.MyCustomException"),
        )
    }

    @Test
    fun `empty class name — Unknown`() {
        assertEquals(
            DomainErrorCategory.Unknown,
            DomainErrorClassifier.categoryFor(""),
        )
    }

    @Test
    fun `match is case-sensitive on class name — class names always are`() {
        // Kotlin / JVM class names are case-sensitive identifiers; pin
        // that the classifier mirrors that so a mistyped "ioexception"
        // doesn't get masked as Network.
        assertEquals(
            DomainErrorCategory.Unknown,
            DomainErrorClassifier.categoryFor("java.io.ioexception"),
        )
    }

    @Test
    fun `precedence — Cancelled wins over Network when both substrings present`() {
        // A composite name like "CancellableIOException" (hypothetical)
        // should still be Cancelled because cancellation propagation
        // is the strongest signal — the caller will throw it back up.
        assertEquals(
            DomainErrorCategory.Cancelled,
            DomainErrorClassifier.categoryFor("CancellationException wrapping IOException"),
        )
    }

    @Test
    fun `classification is deterministic`() {
        val a = DomainErrorClassifier.categoryFor("java.io.IOException")
        val b = DomainErrorClassifier.categoryFor("java.io.IOException")
        assertEquals(a, b)
    }
}
