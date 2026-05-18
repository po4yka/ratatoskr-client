package com.po4yka.ratatoskr.util.storage

import kotlin.test.Test
import kotlin.test.assertEquals

class SecureStorageValueGuardTest {
    @Test
    fun `ordinary ASCII value — Accept`() {
        assertEquals(
            SecureStorageValueGuard.Outcome.Accept,
            SecureStorageValueGuard.check("abc123token"),
        )
    }

    @Test
    fun `null value — RejectNull`() {
        // A null bearer token write should never reach the Keychain;
        // reject at the boundary so the storage layer can return
        // cleanly without a NullPointerException on the platform side.
        assertEquals(
            SecureStorageValueGuard.Outcome.RejectNull,
            SecureStorageValueGuard.check(null),
        )
    }

    @Test
    fun `empty value — Accept — clear-token writes are legal`() {
        // Empty string is the canonical "clear me" sentinel used by
        // logoutWithRevoke; pin that it's distinct from null.
        assertEquals(
            SecureStorageValueGuard.Outcome.Accept,
            SecureStorageValueGuard.check(""),
        )
    }

    @Test
    fun `value with embedded null byte — RejectInvalidChar`() {
        // Some platform implementations (notably older iOS Keychain
        // wrappers) truncate at the first null byte, leading to a
        // silent partial write. Reject at the boundary.
        assertEquals(
            SecureStorageValueGuard.Outcome.RejectInvalidChar,
            SecureStorageValueGuard.check("part1" + Char(0) + "part2"),
        )
    }

    @Test
    fun `value with high control characters — RejectInvalidChar`() {
        // Other ISO control codes also break CSV / ADB tooling.
        assertEquals(
            SecureStorageValueGuard.Outcome.RejectInvalidChar,
            SecureStorageValueGuard.check("bad" + Char(0x01) + "byte"),
        )
        assertEquals(
            SecureStorageValueGuard.Outcome.RejectInvalidChar,
            SecureStorageValueGuard.check("bad" + Char(0x7F) + "byte"),
        )
    }

    @Test
    fun `value at the max length — Accept`() {
        val justRight = "x".repeat(SecureStorageValueGuard.MAX_VALUE_LENGTH)
        assertEquals(
            SecureStorageValueGuard.Outcome.Accept,
            SecureStorageValueGuard.check(justRight),
        )
    }

    @Test
    fun `value above the max length — RejectTooLarge`() {
        val tooLong = "x".repeat(SecureStorageValueGuard.MAX_VALUE_LENGTH + 1)
        assertEquals(
            SecureStorageValueGuard.Outcome.RejectTooLarge,
            SecureStorageValueGuard.check(tooLong),
        )
    }

    @Test
    fun `value containing valid newline + tab — Accept`() {
        // Newlines and tabs are not control codes for our purposes;
        // some long-form tokens embed them legitimately (e.g. PEM
        // bodies in tests). Pin that they're accepted.
        assertEquals(
            SecureStorageValueGuard.Outcome.Accept,
            SecureStorageValueGuard.check("line1\nline2\tindented"),
        )
    }

    @Test
    fun `check is deterministic`() {
        val a = SecureStorageValueGuard.check("abc")
        val b = SecureStorageValueGuard.check("abc")
        assertEquals(a, b)
    }
}
