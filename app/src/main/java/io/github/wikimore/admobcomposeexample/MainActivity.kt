package io.github.wikimore.admobcomposeexample

import android.os.Bundle
import android.widget.ImageView.ScaleType.FIT_CENTER
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import io.github.wikimore.admobcompose.BannerAd
import io.github.wikimore.admobcompose.InterstitialAdState
import io.github.wikimore.admobcompose.NativeAdImage
import io.github.wikimore.admobcompose.NativeAdMediaView
import io.github.wikimore.admobcompose.NativeAdState
import io.github.wikimore.admobcompose.NativeAdView
import io.github.wikimore.admobcompose.NativeAdViewCompose
import io.github.wikimore.admobcompose.RewardAdState
import io.github.wikimore.admobcompose.rememberCustomNativeAdState
import io.github.wikimore.admobcompose.rememberCustomRewardAd
import io.github.wikimore.admobcompose.rememberInterstitialAdState
import io.github.wikimore.admobcomposeexample.states.AdState
import io.github.wikimore.admobcomposeexample.theme.AdMobComposeTheme
import io.github.wikimore.admobcomposeexample.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

const val BANNER_AD_UNIT = "ca-app-pub-3940256099942544/6300978111"
const val INTERSTITIAL_AD_UNIT = "ca-app-pub-3940256099942544/1033173712"
const val NATIVE_AD_AD_UNIT = "ca-app-pub-3940256099942544/2247696110"
const val NATIVE_AD_AD_UNIT_VIDEO = "ca-app-pub-3940256099942544/1044960115"
const val REWARD_AD_AD_UNIT = "ca-app-pub-3940256099942544/5224354917"

class MainActivity : ComponentActivity() {
    val mainViewModel by viewModels<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())
        Timber.d("Timber initialized")
        setContent {
            val bannerAdState by mainViewModel.bannerAdState.collectAsState()
            val interstitialAdState by mainViewModel.interstitialAdState.collectAsState()
            val nativeAdState by mainViewModel.nativeAdState.collectAsState()
            val rewardAdState by mainViewModel.rewardAdState.collectAsState()
            val rememberInterstitialAdState =
                rememberInterstitialAdState(
                    adUnitId = INTERSTITIAL_AD_UNIT,
                    onAdLoaded = {
                        Timber.tag("InterstitialAd").d("onAdLoaded")
                        mainViewModel.updateInterstitialAdState(AdState(isSuccess = true))
                    },
                    onAdLoadFailed = {
                        Timber.tag("InterstitialAd").d("onAdLoadFailed")
                        mainViewModel.updateInterstitialAdState(
                            AdState(
                                isError = true,
                                errorMessage = it.message
                            )
                        )
                    },
                    onAdClicked = {
                        Timber.tag("InterstitialAd").d("onAdClicked")
                    },
                    onAdImpression = {
                        Timber.tag("InterstitialAd").d("onAdImpression")
                    },
                    onAdShowedFullScreenContent = {
                        Timber.tag("InterstitialAd").d("onAdShowedFullScreenContent")
                    },
                    onAdDismissedFullScreenContent = {
                        Timber.tag("InterstitialAd").d("onAdDismissedFullScreenContent")
                    },
                    onAdFailedToShowFullScreenContent = { adError ->
                        mainViewModel.updateInterstitialAdState(
                            AdState(
                                isError = true,
                                errorMessage = adError.message
                            )
                        )
                    },
                    onPaid = { adValue ->
                        Timber.tag("InterstitialAd")
                            .d("onPaid ${adValue.valueMicros}, ${adValue.precisionType}, ${adValue.currencyCode}")
                    }
                )
            val rememberCustomNativeAdState = rememberCustomNativeAdState(
                adUnit = NATIVE_AD_AD_UNIT /*For video need to use set test device configuration*/,
                nativeAdOptions = NativeAdOptions.Builder()
                    .setVideoOptions(
                        VideoOptions.Builder()
                            .setStartMuted(true).setClickToExpandRequested(true)
                            .build()
                    ).setRequestMultipleImages(true)
                    .build(),
                adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        mainViewModel.updateNativeAdState(AdState(isSuccess = true))
                    }

                    override fun onAdFailedToLoad(p0: LoadAdError) {
                        mainViewModel.updateNativeAdState(
                            nativeAdState = AdState(
                                isError = true,
                                errorMessage = p0.message
                            )
                        )
                    }
                }
            )
            val rememberCustomRewardAdState =
                rememberCustomRewardAd(adUnit = REWARD_AD_AD_UNIT, onAdFailedToLoad = {
                    mainViewModel.updateRewardAdState(
                        AdState(
                            isError = true,
                            errorMessage = it.message
                        )
                    )
                }, onAdLoaded = {
                    mainViewModel.updateRewardAdState(AdState(isSuccess = true))
                }, fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                        mainViewModel.updateRewardAdState(
                            AdState(
                                isError = true,
                                errorMessage = p0.message
                            )
                        )
                    }
                })

            AdMobComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier
                            .padding(top = 20.dp)
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        BannerAdsSection(bannerAdState = bannerAdState)
                        Divider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = 1.dp,
                            color = Color.Black.copy(0.3f)
                        )
                        InterstitialAdsSection(
                            interstitialAdState = interstitialAdState,
                            rememberInterstitialAdState = rememberInterstitialAdState
                        )
                        Divider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = 1.dp,
                            color = Color.Black.copy(0.3f)
                        )
                        RewardAdsSection(
                            rewardAdState = rewardAdState,
                            rememberCustomRewardAdState = rememberCustomRewardAdState
                        )
                        Divider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = 1.dp,
                            color = Color.Black.copy(0.3f)
                        )
                        NativeAdsSection(
                            nativeAdState = nativeAdState,
                            rememberNativeAdState = rememberCustomNativeAdState
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun BannerAdsSection(bannerAdState: AdState) {
        Text("Banner Ad", style = TextStyle(fontWeight = FontWeight.Bold))
        BannerAd(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            adUnitId = BANNER_AD_UNIT,
            adSize = AdSize.LARGE_BANNER,
            onAdLoaded = {
                Timber.tag("BannerAd").d("onAdLoaded")
                mainViewModel.updateBannerAdState(AdState(isSuccess = true))
            },
            onAdFailedToLoad = { loadAdError ->
                Timber.tag("BannerAd").d("onAdFailedToLoad")
                mainViewModel.updateBannerAdState(
                    AdState(
                        isError = true,
                        errorMessage = loadAdError.message
                    )
                )
            },
            onAdImpression = {
                Timber.tag("BannerAd").d("onAdImpression")
            },
            onAdClicked = {
                Timber.tag("BannerAd").d("onAdClicked")
            },
            onAdSwipeGestureClicked = {
                Timber.tag("BannerAd").d("onAdSwipeGestureClicked")
            },
            onAdClosed = {
                Timber.tag("BannerAd").d("onAdClosed")
            },
            onAdOpened = {
                Timber.tag("BannerAd").d("onAdOpened")
            },
        )
        when {
            bannerAdState.isSuccess -> Text("BannerAd loaded successfully")
            bannerAdState.isError -> Text("BannerAd load failed: ${bannerAdState.errorMessage}")
        }
    }

    @Composable
    private fun InterstitialAdsSection(
        interstitialAdState: AdState,
        rememberInterstitialAdState: InterstitialAdState?
    ) {
        val hapticFeedback = LocalHapticFeedback.current
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Interstitial Ad", style = TextStyle(fontWeight = FontWeight.Bold))
            AnimatedContent(targetState = interstitialAdState.isSuccess, label = "") { success ->
                if (success)
                    Button(
                        onClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            rememberInterstitialAdState?.show()
                            rememberInterstitialAdState?.refresh()
                        },
                        shape = RoundedCornerShape(40.dp),
                        elevation = ButtonDefaults.buttonElevation(5.dp)
                    ) {
                        Text(text = "Show Interstitial ad")
                    }
                else
                    CircularProgressIndicator()
            }
            when {
                interstitialAdState.isSuccess -> Text(
                    "InterstitialAd loaded successfully",
                    textAlign = TextAlign.Center
                )

                interstitialAdState.isError -> Text(
                    "InterstitialAd load failed: ${interstitialAdState.errorMessage}",
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    @Composable
    private fun NativeAdsSection(
        nativeAdState: AdState,
        rememberNativeAdState: NativeAdState?
    ) {
        rememberNativeAdState?.let {
//            val nativeAd by it.nativeAd.observeAsState()
            Text("Native Ad", style = TextStyle(fontWeight = FontWeight.Bold))
//            NativeAdsDesign(nativeAd)
            when {
                nativeAdState.isSuccess -> Text("Native ad loaded successfully")
                nativeAdState.isError -> Text("Native Ad load failed: ${nativeAdState.errorMessage}")
            }
        }
    }

    @Composable
    private fun NativeAdsDesign(nativeAd: NativeAd?) {
        if (nativeAd != null)
            NativeAdViewCompose { nativeAdView ->
                nativeAdView.setNativeAd(nativeAd)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        //Icon
                        NativeAdView(getView = {
                            nativeAdView.iconView = it
                        }, modifier = Modifier.weight(0.3f)) {
                            NativeAdImage(
                                drawable = nativeAd.icon?.drawable,
                                contentDescription = "Icon",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Column(
                            modifier = Modifier.weight(0.7f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            //Headline
                            NativeAdView(getView = {
                                nativeAdView.headlineView = it
                            }) {
                                Text(
                                    text = nativeAd.headline ?: "-",
                                    style = TextStyle(fontWeight = FontWeight.Bold),
                                    fontSize = 20.sp
                                )
                            }
                            //Body
                            NativeAdView(getView = {
                                nativeAdView.bodyView = it
                            }) {
                                Text(text = nativeAd.body ?: "-", fontSize = 15.sp)
                            }
                        }
                    }
                    //Video
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        nativeAd.mediaContent?.let { mediaContent ->
                            NativeAdMediaView(
                                modifier = Modifier.fillMaxWidth(),
                                nativeAdView = nativeAdView,
                                mediaContent = mediaContent,
                                scaleType = FIT_CENTER
                            )
                            /**Alternate method**/
                            /**Alternate method**/
                            /*NativeAdMediaView(
                                modifier = Modifier.fillMaxWidth()
                            ){
                                nativeAdView.mediaView = it
                                nativeAdView.mediaView?.setImageScaleType(ImageView.ScaleType.FIT_CENTER)
                                nativeAdView.mediaView?.setMediaContent(mediaContent)
                            }*/
                        }
                    }
                }
            }
    }

    @Composable
    private fun RewardAdsSection(
        rewardAdState: AdState,
        rememberCustomRewardAdState: RewardAdState?
    ) {
        val hapticFeedback = LocalHapticFeedback.current
        val coroutineScope = rememberCoroutineScope()
        rememberCustomRewardAdState?.let {
            Text("Reward Ad", style = TextStyle(fontWeight = FontWeight.Bold))
            AnimatedContent(targetState = rewardAdState.isSuccess, label = "") { success ->
                if (success)
                    Button(
                        onClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            coroutineScope.launch {
                                val rewardItem = rememberCustomRewardAdState.showAsync()
                                Timber.tag("RewardItem")
                                    .d("Amount: ${rewardItem.amount} Type: ${rewardItem.type}")
                                delay(1000)
                                rememberCustomRewardAdState.refresh()
                            }
                        },
                        shape = RoundedCornerShape(40.dp),
                        elevation = ButtonDefaults.buttonElevation(5.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text(text = "Show Reward ad")
                    }
                else
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
            }
            when {
                rewardAdState.isSuccess -> Text("Reward ad loaded successfully")
                rewardAdState.isError -> Text("Reward Ad load failed: ${rewardAdState.errorMessage}")
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AdMobComposeTheme {
        Greeting("Android")
    }
}