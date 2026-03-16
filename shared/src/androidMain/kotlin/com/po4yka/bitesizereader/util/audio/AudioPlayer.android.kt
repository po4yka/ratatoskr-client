package com.po4yka.bitesizereader.util.audio

import android.media.MediaPlayer
import java.io.File

actual class AudioPlayer actual constructor() {
    private var mediaPlayer: MediaPlayer? = null
    private var tempFile: File? = null

    actual fun playFromBytes(data: ByteArray) {
        release()
        val file = File.createTempFile("audio_", ".mp3").also { tempFile = it }
        file.writeBytes(data)
        mediaPlayer =
            MediaPlayer().apply {
                setDataSource(file.absolutePath)
                prepare()
                start()
            }
    }

    actual fun pause() {
        mediaPlayer?.takeIf { it.isPlaying }?.pause()
    }

    actual fun resume() {
        mediaPlayer?.start()
    }

    actual fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.reset()
    }

    actual fun seekTo(positionMs: Long) {
        mediaPlayer?.seekTo(positionMs.toInt())
    }

    actual fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
        tempFile?.delete()
        tempFile = null
    }

    actual val isPlaying: Boolean
        get() = mediaPlayer?.isPlaying == true

    actual val currentPositionMs: Long
        get() = mediaPlayer?.currentPosition?.toLong() ?: 0L

    actual val durationMs: Long
        get() = mediaPlayer?.duration?.toLong() ?: 0L
}
