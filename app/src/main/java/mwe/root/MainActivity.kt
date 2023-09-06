package mwe.root

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ConcatenatingMediaSource
import androidx.media3.exoplayer.source.ShuffleOrder
import kotlinx.coroutines.runBlocking

internal class MainActivity : Activity() {
    private lateinit var concatenatingMediaSourceController: ConcatenatingMediaSourceController
    private val exoPlayerControlHandler = HandlerThread("ExoPlayerControlThread").run {
        start()
        Handler(looper)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val concatenatingMediaSource =
            ConcatenatingMediaSource(false, true, ShuffleOrder.DefaultShuffleOrder(0))
        val exoPlayer = ExoPlayer.Builder(this)
            .setLooper(exoPlayerControlHandler.looper)
            .build()
        exoPlayerControlHandler.post {
            exoPlayer.apply {
                addAnalyticsListener(DebugAnalyticsLister())
                setMediaSource(concatenatingMediaSource)
                playWhenReady = true
                prepare()
            }
        }
        concatenatingMediaSourceController =
            ConcatenatingMediaSourceController(concatenatingMediaSource)
        val uriMediaSourceFactory = UriMediaSourceFactory(DefaultDataSource.Factory(this))
        onMediaSourceController {
            addMediaSourceSync(
                0,
                uriMediaSourceFactory.create(
                    Uri.parse(
                        "https://storage.googleapis.com/exoplayer-test-media-1/60fps/bbb-clear-1080/audio.mp4"
                    )
                )
            )
        }
        onMediaSourceController {
            addMediaSourceSync(
                1,
                uriMediaSourceFactory.create(
                    Uri.parse(
                        "https://bestvpn.org/html5demos/assets/dizzy.mp4"
                    )
                )
            )
        }
        onMediaSourceController {
            addMediaSourceSync(
                1,
                uriMediaSourceFactory.create(
                    Uri.parse(
                        "https://storage.googleapis.com/wvmedia/clear/h264/tears/tears_audio_eng.mp4"
                    )
                )
            )
        }
        onMediaSourceController { removeMediaSourceSync(concatenatingMediaSource.size - 1) }
        exoPlayerControlHandler.post { exoPlayer.seekToNext() }
        customLogD("Finished dispatching all commands")
    }

    private fun onMediaSourceController(
        block: suspend ConcatenatingMediaSourceController.() -> Unit
    ) = exoPlayerControlHandler.post { runBlocking { concatenatingMediaSourceController.block() } }
}
