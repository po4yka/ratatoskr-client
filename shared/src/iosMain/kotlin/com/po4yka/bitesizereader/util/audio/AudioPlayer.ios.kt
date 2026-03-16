package com.po4yka.bitesizereader.util.audio

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.AVFAudio.AVAudioPlayer
import platform.Foundation.NSData
import platform.Foundation.create
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class)
actual class AudioPlayer actual constructor() {
    private var player: AVAudioPlayer? = null

    actual fun playFromBytes(data: ByteArray) {
        release()
        val nsData = data.toNSData()
        player = AVAudioPlayer(data = nsData, error = null)
        player?.prepareToPlay()
        player?.play()
    }

    actual fun pause() {
        player?.pause()
    }

    actual fun resume() {
        player?.play()
    }

    actual fun stop() {
        player?.stop()
        player?.currentTime = 0.0
    }

    actual fun seekTo(positionMs: Long) {
        player?.currentTime = positionMs / 1000.0
    }

    actual fun release() {
        player?.stop()
        player = null
    }

    actual val isPlaying: Boolean
        get() = player?.isPlaying() == true

    actual val currentPositionMs: Long
        get() = ((player?.currentTime ?: 0.0) * 1000).toLong()

    actual val durationMs: Long
        get() = ((player?.duration ?: 0.0) * 1000).toLong()

    @Suppress("unused")
    private fun ByteArray.toNSData(): NSData {
        if (isEmpty()) return NSData()
        return usePinned { pinned ->
            NSData.create(bytes = pinned.addressOf(0), length = size.toULong())
        }
    }
}
