package io.github.ovso.exocachetest

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_main.playerview
import timber.log.Timber

class MainActivity : AppCompatActivity() {

  private val FORMAT_MP4 = "mp4"
  private val FORMAT_M3U8 = "m3u8"

  lateinit var player: SimpleExoPlayer
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    }

    player = ExoPlayerFactory.newSimpleInstance(this)
    player.repeatMode = Player.REPEAT_MODE_ONE
    player.addListener(eventListener)
    playerview.player = player
    player.prepare(getMediaSource())
    player.playWhenReady = true
  }

  private fun getMediaSource(): MediaSource {
    val uri = Uri.parse("https://eb.triphi.com/city/mfv/1213625/1213625_01.mp4?update_date=2019-05-30T03:21:57Z")
    val userAgent = Util.getUserAgent(this, "Triphi")
    val factory = DefaultDataSourceFactory(this, userAgent)
    val lastPathSegment = uri.lastPathSegment
    return if (!TextUtils.isEmpty(lastPathSegment) && lastPathSegment!!.contains(FORMAT_M3U8)) {
      HlsMediaSource.Factory(factory).createMediaSource(uri)
    } else if (!TextUtils.isEmpty(lastPathSegment) && lastPathSegment!!.contains(FORMAT_MP4)) {
      ExtractorMediaSource.Factory(factory).createMediaSource(uri)
    } else {
      ExtractorMediaSource.Factory(factory).createMediaSource(uri)
    }
  }

  private val eventListener = object : Player.EventListener {
    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
      val stateString: String
      when (playbackState) {
        Player.STATE_IDLE -> {
          stateString = "Player.STATE_IDLE      -"
        }
        Player.STATE_BUFFERING -> {
          stateString = "Player.STATE_BUFFERING -"
        }
        Player.STATE_READY -> {
          stateString = "Player.STATE_READY     -"
        }
        Player.STATE_ENDED -> stateString = "Player.STATE_ENDED     -"
        else -> {
          stateString = "UNKNOWN_STATE             -"
        }
      }
      Timber.d(
        "MainRecCityVideo changed state to "
            + stateString
            + " playWhenReady: "
            + playWhenReady
      )
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    player.removeListener(eventListener)
  }
}
