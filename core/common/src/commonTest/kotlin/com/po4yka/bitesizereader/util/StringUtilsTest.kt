package com.po4yka.bitesizereader.util

import kotlin.test.Test
import kotlin.test.assertEquals

class StringUtilsTest {
    @Test
    fun `redactQueryAndFragment removes query parameters`() {
        assertEquals(
            "https://example.com/callback",
            "https://example.com/callback?token=secret&id=1".redactQueryAndFragment(),
        )
    }

    @Test
    fun `redactQueryAndFragment removes fragments`() {
        assertEquals(
            "https://example.com/callback",
            "https://example.com/callback#access_token=secret".redactQueryAndFragment(),
        )
    }

    @Test
    fun `redactQueryAndFragment keeps plain urls unchanged`() {
        assertEquals(
            "https://example.com/callback",
            "https://example.com/callback".redactQueryAndFragment(),
        )
    }
}
