package mwe.root

import android.net.Uri
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.util.Util
import androidx.media3.datasource.DataSource
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource

internal class UriMediaSourceFactory(dataSourceFactory: DataSource.Factory) {
    private val dashMediaSourceFactory = DashMediaSource.Factory(dataSourceFactory)
    private val progressiveMediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)

    fun create(uri: Uri) = MediaItem.Builder()
        .setUri(uri)
        .setMediaId(uri.toString().substringAfterLast('/'))
        .build().run {
            when (uri.contentTypeInferredFromExtension) {
                C.CONTENT_TYPE_DASH -> dashMediaSourceFactory.createMediaSource(this)
                else -> progressiveMediaSource.createMediaSource(this)
            }
        }

    private val Uri.contentTypeInferredFromExtension: Int
        get() = Util.inferContentTypeForExtension(toString().substringAfterLast('.'))
}
