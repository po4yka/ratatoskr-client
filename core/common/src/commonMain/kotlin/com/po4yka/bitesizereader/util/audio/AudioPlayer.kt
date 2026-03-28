package com.po4yka.bitesizereader.util.audio

/**
 * Cross-platform audio player for MP3 playback.
 * Platform implementations use MediaPlayer (Android), AVAudioPlayer (iOS), and no-op (Desktop).
 */
expect class AudioPlayer() {
    fun playFromBytes(data: ByteArray)

    fun pause()

    fun resume()

    fun stop()

    fun seekTo(positionMs: Long)

    fun release()

    val isPlaying: Boolean
    val currentPositionMs: Long
    val durationMs: Long
}
