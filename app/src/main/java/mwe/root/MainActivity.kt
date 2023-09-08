package mwe.root

import android.app.Activity
import android.net.Uri
import android.os.Handler
import android.os.HandlerThread
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ConcatenatingMediaSource
import androidx.media3.exoplayer.source.ShuffleOrder
import androidx.media3.exoplayer.util.EventLogger
import kotlinx.coroutines.runBlocking

internal class MainActivity : Activity() {
    private var exoPlayer: ExoPlayer? = null
    private lateinit var mediaSourceController: MediaSourceController

    private val otherThread = HandlerThread("ExoPlayerControlThread").run {
        start()
        Handler(looper)
    }

    override fun onStart() {
        super.onStart()
        val player = ExoPlayer.Builder(this)
            .build()
        exoPlayer = player
        player.apply {
            addAnalyticsListener(DebugAnalyticsLister())
            playWhenReady = true
            addAnalyticsListener(EventLogger())
        }
        mediaSourceController = MediaSourceController(player)
        val uriMediaSourceFactory = UriMediaSourceFactory(DefaultDataSource.Factory(this))

        otherThread.post {
            mediaSourceController.addMediaSource(
                    0,
                    uriMediaSourceFactory.create(
                        Uri.parse(
                            "https://storage.googleapis.com/exoplayer-test-media-1/60fps/bbb-clear-1080/audio.mp4"
                        )
                    )
                ) {
                    customLogD("Finished addMediaSource for item at index 0")
                    player.prepare()
                }
        }

        otherThread.post {
            mediaSourceController.addMediaSource(
                1,
                uriMediaSourceFactory.create(
                    Uri.parse(
                        "https://bestvpn.org/html5demos/assets/dizzy.mp4"
                    )
                )
            ) {
                customLogD("Finished addMediaSource for item at index 1")
            }
        }

        otherThread.post {
            mediaSourceController.addMediaSource(
                1,
                uriMediaSourceFactory.create(
                    Uri.parse(
                        "https://storage.googleapis.com/wvmedia/clear/h264/tears/tears_audio_eng.mp4"
                    )
                )
            ) {
                customLogD("Finished addMediaSource for item at index 1")
                mediaSourceController.removeMediaSource(1) {
                    customLogD("Finished removeMediaSource on index 1")
                    player.seekToNext()
                    customLogD("Finished dispatching all commands")
                }
            }
        }
    }

    override fun onStop() {
        exoPlayer?.run {
            stop()
            release()
        }
        super.onStop()
    }
}
