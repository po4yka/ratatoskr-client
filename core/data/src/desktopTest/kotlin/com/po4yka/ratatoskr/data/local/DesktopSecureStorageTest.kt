package com.po4yka.ratatoskr.data.local

class DesktopSecureStorageTest : SecureStorageContract() {
    override suspend fun createStorage(): SecureStorage = DesktopSecureStorage()
}
