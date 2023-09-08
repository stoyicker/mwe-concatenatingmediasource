package mwe.root

import android.os.Handler
import android.os.HandlerThread
import androidx.media3.exoplayer.source.ConcatenatingMediaSource
import androidx.media3.exoplayer.source.MediaSource
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

internal class ConcatenatingMediaSourceController(
    private val concatenatingMediaSource: ConcatenatingMediaSource
) {
    private val mutex = Mutex()
    private val handler = HandlerThread("ConcatenatingMediaSourceControllerThread").run {
        start()
        Handler(looper)
    }

    suspend fun addMediaSourceSync(index: Int, mediaSource: MediaSource) = mutex.withLock {
        withContext(currentCoroutineContext()) {
            launch {
                concatenatingMediaSource.addMediaSource(index, mediaSource, handler) {
                    customLogD("Finished addition on for item id=${mediaSource.mediaItem.mediaId} index $index")
                    mutex.unlock()
                }
            }
        }
        customLogD("Waiting for addition for item id=${mediaSource.mediaItem.mediaId} index $index")
        mutex.lock()
        customLogD("Finished addMediaSource for item id=${mediaSource.mediaItem.mediaId} on index $index")
    }

    suspend fun removeMediaSourceSync(index: Int) = mutex.withLock {
        withContext(currentCoroutineContext()) {
            launch {
                concatenatingMediaSource.removeMediaSource(index, handler) {
                    customLogD("Finished deletion for index $index")
                    mutex.unlock()
                }
            }
        }
        customLogD("Waiting for deletion for index $index")
        mutex.lock()
        customLogD("Finished removeMediaSourceSync on index $index")
    }
}
