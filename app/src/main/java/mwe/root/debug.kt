package mwe.root

import android.util.Log
import androidx.media3.common.Player

internal val Int.playerStateToString: String
    get() = when (this) {
        Player.STATE_IDLE -> "IDLE"
        Player.STATE_READY -> "READY"
        Player.STATE_BUFFERING -> "BUFFERING"
        Player.STATE_ENDED -> "ENDED"
        else -> throw IllegalArgumentException("Illegal player state $this")
    }

internal fun customLogD(msg: String) {
    Log.d("TEST-Thread[${Thread.currentThread().name}]", msg)
}
