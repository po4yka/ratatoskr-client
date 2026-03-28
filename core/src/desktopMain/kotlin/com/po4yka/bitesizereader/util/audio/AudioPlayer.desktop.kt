package com.po4yka.bitesizereader.util.audio

/**
 * Desktop stub for AudioPlayer. Desktop is development-only (hot reload),
 * so audio playback is not implemented.
 */
actual class AudioPlayer actual constructor() {
    actual fun playFromBytes(data: ByteArray) { /* no-op */ }

    actual fun pause() { /* no-op */ }

    actual fun resume() { /* no-op */ }

    actual fun stop() { /* no-op */ }

    actual fun seekTo(positionMs: Long) { /* no-op */ }

    actual fun release() { /* no-op */ }

    actual val isPlaying: Boolean get() = false
    actual val currentPositionMs: Long get() = 0L
    actual val durationMs: Long get() = 0L
}
