package com.po4yka.ratatoskr.feature.summary.export

object ObsidianDeepLink {
    fun composeNewNote(
        vault: String,
        name: String,
        content: String,
    ): String =
        buildString {
            append("obsidian://new?")
            val parts = mutableListOf<String>()
            if (vault.isNotBlank()) parts += "vault=" + percentEncode(vault)
            parts += "name=" + percentEncode(name)
            parts += "content=" + percentEncode(content)
            append(parts.joinToString("&"))
        }

    private fun percentEncode(value: String): String {
        val bytes = value.encodeToByteArray()
        val sb = StringBuilder(bytes.size)
        for (b in bytes) {
            val unsigned = b.toInt() and 0xFF
            if (isUnreserved(unsigned)) {
                sb.append(unsigned.toChar())
            } else {
                sb.append('%')
                sb.append(HEX[(unsigned ushr 4) and 0x0F])
                sb.append(HEX[unsigned and 0x0F])
            }
        }
        return sb.toString()
    }

    private fun isUnreserved(byte: Int): Boolean =
        (byte in 'A'.code..'Z'.code) ||
            (byte in 'a'.code..'z'.code) ||
            (byte in '0'.code..'9'.code) ||
            byte == '-'.code ||
            byte == '_'.code ||
            byte == '.'.code ||
            byte == '~'.code

    private val HEX = "0123456789ABCDEF".toCharArray()
}
