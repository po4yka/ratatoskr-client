package com.po4yka.ratatoskr.data.local

/**
 * iOS-side contract run against the real [IosSecureStorage], which talks to the system
 * Keychain via `multiplatform-settings`'s `KeychainSettings`. The Keychain entries persist
 * across test methods within a single run, so each test exercising "clear" semantics calls
 * `clearTokens` / `clearDeveloperCredentials` itself before asserting absence.
 */
class IosSecureStorageTest : SecureStorageContract() {
    override suspend fun createStorage(): SecureStorage {
        val storage = IosSecureStorage()
        // Ensure each test starts from a clean Keychain state — the service name is
        // shared across all test methods so a previous test's leftover would leak.
        storage.clearTokens()
        storage.clearDeveloperCredentials()
        return storage
    }

    override suspend fun recreateAgainstSameStore(existing: SecureStorage): SecureStorage {
        // The Keychain service name is hardcoded inside `IosSecureStorage`, so building a
        // second instance points it at the same backing store — and exercises the contract's
        // cross-instance round-trip / Keychain-entry reuse assertions for free.
        return IosSecureStorage()
    }
}
