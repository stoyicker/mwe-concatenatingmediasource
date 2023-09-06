package mwe.root

import androidx.media3.exoplayer.analytics.AnalyticsListener

internal class DebugAnalyticsLister : AnalyticsListener {

    override fun onPlaybackStateChanged(eventTime: AnalyticsListener.EventTime, state: Int) {
        customLogD("Received new playback state ${state.playerStateToString}")
    }

    override fun onPlayWhenReadyChanged(
        eventTime: AnalyticsListener.EventTime,
        playWhenReady: Boolean,
        reason: Int
    ) {
        customLogD("playWhenReady changed to $playWhenReady")
    }
}
