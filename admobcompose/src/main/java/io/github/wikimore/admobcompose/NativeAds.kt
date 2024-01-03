package io.github.wikimore.admobcompose

import android.app.Activity
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MediaContent
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView

@Composable
fun NativeAdViewCompose(
    modifier: Modifier = Modifier,
    content: @Composable (nativeAdView: NativeAdView) -> Unit
) =
    AndroidView(modifier = modifier, factory = {
        NativeAdView(it)
    }, update = {
        val composeView = ComposeView(it.context)
        it.removeAllViews()
        it.addView(composeView)
        composeView.setContent {
            content(it)
        }
    })

@Composable
fun NativeAdView(
    modifier: Modifier = Modifier, getView: (ComposeView) -> Unit, content: @Composable () -> Unit
) = AndroidView(modifier = modifier, factory = { ComposeView(it) }, update = {
    it.setContent {
        content()
    }
    getView(it)
})

@Composable
fun NativeAdImage(
    modifier: Modifier = Modifier,
    drawable: Drawable?,
    contentDescription: String,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,

    ) = Image(
    painter = rememberDrawablePainter(drawable = drawable),
    contentDescription = contentDescription,
    contentScale = contentScale,
    modifier = modifier,
    colorFilter = colorFilter,
    alpha = alpha,
    alignment = alignment,
)

@Composable
fun NativeAdMediaView(modifier: Modifier = Modifier, setup: (MediaView) -> Unit) =
    AndroidView(modifier = modifier, factory = { MediaView(it) }, update = {
        setup(it)
    })

@Composable
fun NativeAdMediaView(
    modifier: Modifier = Modifier,
    nativeAdView: NativeAdView,
    mediaContent: MediaContent,
    scaleType: ImageView.ScaleType
) = AndroidView(modifier = modifier, factory = { MediaView(it) }, update = {
    nativeAdView.mediaView = it
    nativeAdView.mediaView?.mediaContent = mediaContent
    nativeAdView.mediaView?.setImageScaleType(scaleType)
})

@Composable
fun rememberCustomNativeAdState(
    adUnit: String,
    adListener: AdListener? = null,
    nativeAdOptions: NativeAdOptions? = null
) = LocalContext.current.getActivity()?.let {
    remember(adUnit) {
        NativeAdState(
            activity = it,
            adUnit = adUnit,
            adListener = adListener,
            adOptions = nativeAdOptions
        )
    }
}

class NativeAdState(
    activity: Activity,
    adUnit: String,
    adListener: AdListener?,
    adOptions: NativeAdOptions?
) {
    init {
        AdLoader.Builder(activity, adUnit).let {
            if (adOptions != null)
                it.withNativeAdOptions(adOptions)
            else
                it
        }.let {
            if (adListener != null)
                it.withAdListener(adListener)
            else
                it
        }.forNativeAd { nativeAd ->
            if (activity.isDestroyed) {
                nativeAd.destroy()
                return@forNativeAd
            }

        }.build().loadAd(AdRequest.Builder().build())
    }
}