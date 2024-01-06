package io.github.wikimore.admobcompose

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdValue
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnPaidEventListener
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

@Composable
fun rememberInterstitialAdState(
    adUnitId: String,
    onAdLoaded: () -> Unit = { },
    onAdLoadFailed: (adError: LoadAdError) -> Unit = { },
    onAdClicked: () -> Unit = { },
    onAdImpression: () -> Unit = { },
    onAdShowedFullScreenContent: () -> Unit = { },
    onAdDismissedFullScreenContent: () -> Unit = { },
    onAdFailedToShowFullScreenContent: (adError: AdError) -> Unit = { },
    onPaid: (adValue: AdValue) -> Unit = { }
): InterstitialAdState? {
    val context = LocalContext.current
    context.getActivity()?.let {
        return remember(adUnitId) {
            InterstitialAdState(
                activity = it,
                adUnitId = adUnitId,
                onAdLoadFailed = onAdLoadFailed,
                onAdLoaded = onAdLoaded,
                onAdClicked = onAdClicked,
                onAdImpression = onAdImpression,
                onAdShowedFullScreenContent = onAdShowedFullScreenContent,
                onAdDismissedFullScreenContent = onAdDismissedFullScreenContent,
                onAdFailedToShowFullScreenContent = onAdFailedToShowFullScreenContent,
                onPaid = onPaid,
            )
        }
    }
    return null
}

class InterstitialAdState(
    private val activity: Activity,
    private val adUnitId: String,
    private val onAdLoaded: () -> Unit,
    private val onAdLoadFailed: (adError: LoadAdError) -> Unit,
    private val onAdClicked: () -> Unit,
    private val onAdImpression: () -> Unit,
    private val onAdShowedFullScreenContent: () -> Unit,
    private val onAdDismissedFullScreenContent: () -> Unit,
    private val onAdFailedToShowFullScreenContent: (adError: AdError) -> Unit,
    private val onPaid: (adValue: AdValue) -> Unit
) {
    private var mInterstitialAd: InterstitialAd? = null

    init {
        load(adUnitId)
    }

    private fun load(adUnitId: String) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            activity,
            adUnitId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mInterstitialAd = null
                    onAdLoadFailed.invoke(adError)
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                    mInterstitialAd?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdClicked() {
                                onAdClicked.invoke()
                            }

                            override fun onAdDismissedFullScreenContent() {
                                onAdDismissedFullScreenContent.invoke()
                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                onAdFailedToShowFullScreenContent.invoke(adError)
                            }

                            override fun onAdImpression() {
                                onAdImpression.invoke()
                            }

                            override fun onAdShowedFullScreenContent() {
                                onAdShowedFullScreenContent.invoke()
                            }
                        }
                    mInterstitialAd?.setOnPaidEventListener { adValue ->
                        onPaid.invoke(adValue)
                    }
                    onAdLoaded.invoke()
                }
            })
    }

    fun show() {
        mInterstitialAd?.show(activity)
    }

    fun show(fullScreenContentCallback: FullScreenContentCallback) {
        mInterstitialAd?.fullScreenContentCallback = fullScreenContentCallback
        mInterstitialAd?.show(activity)
    }

    fun show(
        fullScreenContentCallback: FullScreenContentCallback,
        onPaidEventListener: OnPaidEventListener
    ) {
        mInterstitialAd?.fullScreenContentCallback = fullScreenContentCallback
        mInterstitialAd?.onPaidEventListener = onPaidEventListener
        mInterstitialAd?.show(activity)
    }

    fun show(
        onAdShowed: (() -> Unit)? = null,
        onAdImpression: (() -> Unit)? = null,
        onAdClosed: (() -> Unit)? = null,
        onAdClicked: (() -> Unit)? = null
    ) {
        mInterstitialAd?.fullScreenContentCallback =
            object : FullScreenContentCallback() {
                override fun onAdClicked() {
                    onAdClicked?.invoke()?.let {
                        this@InterstitialAdState.onAdClicked.invoke()
                    }
                }

                override fun onAdDismissedFullScreenContent() {
                    onAdClosed?.invoke()?.let {
                        this@InterstitialAdState.onAdDismissedFullScreenContent.invoke()
                    }
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    this@InterstitialAdState.onAdFailedToShowFullScreenContent.invoke(adError)
                }

                override fun onAdImpression() {
                    onAdImpression?.invoke()?.let {
                        this@InterstitialAdState.onAdImpression.invoke()
                    }
                }

                override fun onAdShowedFullScreenContent() {
                    onAdShowed?.invoke()?.let {
                        this@InterstitialAdState.onAdShowedFullScreenContent.invoke()
                    }
                }
            }
        mInterstitialAd?.show(activity)
    }

    fun refresh(adUnitId: String? = null) {
        load(adUnitId = adUnitId ?: this.adUnitId)
    }
}
