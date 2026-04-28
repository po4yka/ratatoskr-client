package com.po4yka.ratatoskr.feature.auth.util

internal object UrlDecoder {
    fun decode(part: String): String {
        if ('%' !in part && '+' !in part) return part

        val result = StringBuilder(part.length)
        val decodedBytes = mutableListOf<Byte>()

        fun flushDecodedBytes() {
            if (decodedBytes.isEmpty()) return
            result.append(decodedBytes.toByteArray().decodeToString())
            decodedBytes.clear()
        }

        var index = 0
        while (index < part.length) {
            when (val current = part[index]) {
                '+' -> {
                    flushDecodedBytes()
                    result.append(' ')
                    index += 1
                }

                '%' -> {
                    val high = part.getOrNull(index + 1)?.digitToIntOrNull(16)
                    val low = part.getOrNull(index + 2)?.digitToIntOrNull(16)
                    if (high != null && low != null) {
                        decodedBytes += ((high shl 4) + low).toByte()
                        index += 3
                    } else {
                        flushDecodedBytes()
                        result.append(current)
                        index += 1
                    }
                }

                else -> {
                    flushDecodedBytes()
                    result.append(current)
                    index += 1
                }
            }
        }

        flushDecodedBytes()
        return result.toString()
    }
}
