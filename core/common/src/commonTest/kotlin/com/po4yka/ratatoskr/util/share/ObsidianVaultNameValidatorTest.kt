package com.po4yka.ratatoskr.util.share

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ObsidianVaultNameValidatorTest {
    @Test
    fun `null, empty, and whitespace-only input collapses to Empty`() {
        // Empty is a first-class result — the Settings field defaults to
        // empty so that the Obsidian deep-link omits the vault parameter
        // and Obsidian opens whichever vault was last active.
        assertEquals(ObsidianVaultNameValidator.Result.Empty, ObsidianVaultNameValidator.validate(null))
        assertEquals(ObsidianVaultNameValidator.Result.Empty, ObsidianVaultNameValidator.validate(""))
        assertEquals(ObsidianVaultNameValidator.Result.Empty, ObsidianVaultNameValidator.validate("   "))
        assertEquals(ObsidianVaultNameValidator.Result.Empty, ObsidianVaultNameValidator.validate("\t\n"))
    }

    @Test
    fun `surrounding whitespace is trimmed silently from Valid output`() {
        // Users paste vault names — trailing spaces from a copy-paste must
        // not turn the input into an Invalid; trim quietly and store the
        // clean form.
        val result = ObsidianVaultNameValidator.validate("  MyVault  ")
        assertIs<ObsidianVaultNameValidator.Result.Valid>(result)
        assertEquals("MyVault", result.name)
    }

    @Test
    fun `plain ASCII vault name passes validation`() {
        val result = ObsidianVaultNameValidator.validate("MyVault")
        assertIs<ObsidianVaultNameValidator.Result.Valid>(result)
        assertEquals("MyVault", result.name)
    }

    @Test
    fun `vault name with embedded spaces is accepted`() {
        val result = ObsidianVaultNameValidator.validate("Reading Notes 2026")
        assertIs<ObsidianVaultNameValidator.Result.Valid>(result)
        assertEquals("Reading Notes 2026", result.name)
    }

    @Test
    fun `non-Latin vault names are accepted — Cyrillic, CJK, Emoji`() {
        // Obsidian supports Unicode folder names; the validator must not
        // reject locale-specific characters. Russian, Chinese, and even
        // emoji-named vaults are valid on macOS and Linux filesystems.
        assertIs<ObsidianVaultNameValidator.Result.Valid>(
            ObsidianVaultNameValidator.validate("Заметки"),
        )
        assertIs<ObsidianVaultNameValidator.Result.Valid>(
            ObsidianVaultNameValidator.validate("笔记"),
        )
        assertIs<ObsidianVaultNameValidator.Result.Valid>(
            ObsidianVaultNameValidator.validate("📚 Library"),
        )
    }

    @Test
    fun `forward slash is rejected as path separator`() {
        // Vault is a name, not a path — the deep-link consumer (Obsidian)
        // interprets `/` in the vault parameter as a folder boundary,
        // which silently routes the new note into the wrong vault.
        val result = ObsidianVaultNameValidator.validate("My/Vault")
        assertIs<ObsidianVaultNameValidator.Result.Invalid>(result)
        assertEquals(ObsidianVaultNameValidator.Reason.ContainsPathSeparator, result.reason)
    }

    @Test
    fun `backslash is rejected as path separator`() {
        // Same rationale as forward slash — Windows path separator must
        // be rejected so a vault name does not double as a filesystem path.
        val result = ObsidianVaultNameValidator.validate("My\\Vault")
        assertIs<ObsidianVaultNameValidator.Result.Invalid>(result)
        assertEquals(ObsidianVaultNameValidator.Reason.ContainsPathSeparator, result.reason)
    }

    @Test
    fun `colon is rejected as reserved character`() {
        // macOS HFS+ treats `:` as the legacy path separator; Windows
        // forbids it in filenames. Either OS surfacing the resulting
        // vault folder would error.
        val result = ObsidianVaultNameValidator.validate("My:Vault")
        assertIs<ObsidianVaultNameValidator.Result.Invalid>(result)
        assertEquals(ObsidianVaultNameValidator.Reason.ContainsReservedChar, result.reason)
    }

    @Test
    fun `null byte inside the name is rejected as control character`() {
        // Defense against header-injection-style attacks if the value
        // ever flows into a query string without re-encoding. We build
        // the null byte at runtime via Char(0) so the test source stays
        // plain UTF-8 (no embedded control bytes).
        val withNul = "My" + Char(0) + "Vault"
        val result = ObsidianVaultNameValidator.validate(withNul)
        assertIs<ObsidianVaultNameValidator.Result.Invalid>(result)
        assertEquals(ObsidianVaultNameValidator.Reason.ContainsControlChar, result.reason)
    }

    @Test
    fun `newline and tab embedded in the name are rejected as control characters`() {
        // Trim handles surrounding whitespace; embedded controls always
        // indicate a paste accident or pasted CRLF from a markdown table.
        assertIs<ObsidianVaultNameValidator.Result.Invalid>(
            ObsidianVaultNameValidator.validate("My\nVault"),
        )
        assertIs<ObsidianVaultNameValidator.Result.Invalid>(
            ObsidianVaultNameValidator.validate("My\tVault"),
        )
    }

    @Test
    fun `names exceeding the max length are rejected`() {
        // 200-char limit matches the most restrictive common filesystem
        // (older ext4 and Windows MAX_PATH-adjacent guidance). One char
        // past the limit triggers the bail.
        val justFits = "a".repeat(200)
        val tooLong = "a".repeat(201)
        assertIs<ObsidianVaultNameValidator.Result.Valid>(
            ObsidianVaultNameValidator.validate(justFits),
        )
        val result = ObsidianVaultNameValidator.validate(tooLong)
        assertIs<ObsidianVaultNameValidator.Result.Invalid>(result)
        assertEquals(ObsidianVaultNameValidator.Reason.ExceedsMaxLength, result.reason)
    }

    @Test
    fun `dots, dashes, underscores, and parentheses are accepted`() {
        // Common naming patterns: "obsidian-vault.v2", "vault_2026",
        // "Reading (Personal)" should all pass.
        assertIs<ObsidianVaultNameValidator.Result.Valid>(
            ObsidianVaultNameValidator.validate("obsidian-vault.v2"),
        )
        assertIs<ObsidianVaultNameValidator.Result.Valid>(
            ObsidianVaultNameValidator.validate("vault_2026"),
        )
        assertIs<ObsidianVaultNameValidator.Result.Valid>(
            ObsidianVaultNameValidator.validate("Reading (Personal)"),
        )
    }

    @Test
    fun `length is measured after trimming, not before`() {
        // 200 valid chars surrounded by whitespace must still pass —
        // otherwise paste-with-padding becomes a confusing false fail.
        val padded = "  " + "a".repeat(200) + "  "
        val result = ObsidianVaultNameValidator.validate(padded)
        assertIs<ObsidianVaultNameValidator.Result.Valid>(result)
        assertEquals(200, result.name.length)
    }
}
