package mwe.root

import android.os.Handler
import android.os.Looper
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource

internal class MediaSourceController(val exoPlayer: ExoPlayer) {

    private val handler = Handler(Looper.getMainLooper())

    fun addMediaSource(index: Int, mediaSource: MediaSource, onCompletion: Runnable) {
        handler.post {
            exoPlayer.addMediaSource(index, mediaSource)
            onCompletion.run()
        }
    }

    fun removeMediaSource(index: Int, onCompletion: Runnable) {
        handler.post {
            exoPlayer.removeMediaItem(index)
            onCompletion.run()
        }
    }
}
