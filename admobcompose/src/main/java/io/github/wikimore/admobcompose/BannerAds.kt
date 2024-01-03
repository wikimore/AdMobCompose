package io.github.wikimore.admobcompose

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

@Composable
fun BannerAd(
    adUnitId: String,
    modifier: Modifier,
    adSize: AdSize = AdSize.BANNER,
    onAdLoaded: () -> Unit = {},
    onAdFailedToLoad: (adLoadError: LoadAdError) -> Unit = {},
    onAdImpression: () -> Unit = {},
    onAdClicked: () -> Unit = {},
    onAdSwipeGestureClicked: () -> Unit = {},
    onAdOpened: () -> Unit = {},
    onAdClosed: () -> Unit = {}
) {
    AndroidView(
        factory = { context ->
            AdView(context).apply {
                setAdUnitId(adUnitId)
                setAdSize(adSize)
                adListener = object : AdListener() {
                    override fun onAdClicked() {
                        onAdClicked.invoke()
                    }

                    override fun onAdClosed() {
                        onAdClosed.invoke()
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        onAdFailedToLoad.invoke(loadAdError)
                    }

                    override fun onAdImpression() {
                        onAdImpression.invoke()
                    }

                    override fun onAdLoaded() {
                        onAdLoaded.invoke()
                    }

                    override fun onAdOpened() {
                        onAdOpened.invoke()
                    }

                    override fun onAdSwipeGestureClicked() {
                        onAdSwipeGestureClicked.invoke()
                    }
                }
                loadAd(AdRequest.Builder().build())
            }
        }, modifier = modifier.fillMaxWidth()
    )
}