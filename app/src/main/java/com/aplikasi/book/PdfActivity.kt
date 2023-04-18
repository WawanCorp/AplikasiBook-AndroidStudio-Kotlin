package com.aplikasi.book

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.constraintlayout.helper.widget.MotionEffect
import com.aplikasi.book.databinding.ActivityPdfBinding
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class PdfActivity : AppCompatActivity() {
    private var mInterstitialAd: InterstitialAd? = null
    private lateinit var binding: ActivityPdfBinding
    private lateinit var mAdView: AdView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        loadBannerAd()
        loadInterAd()
        btnBackListener()

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)


        binding.pdfView.fromAsset("basis-data-xii.pdf")
            .enableSwipe(true)
            .swipeHorizontal(true)
            .enableDoubletap(true)
            .onDraw { canvas, pageWidth, pageHeight, displayedPage ->

            }.onDrawAll { canvas, pageWidth, pageHeight, displayedPage ->

            }
            .onPageChange { page, pageCount ->

            }.onPageError { page, t ->
                Toast.makeText(
                    this@PdfActivity,
                    "Error while opening page" + page,
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("ERROR", "" + t.localizedMessage)
            }

            .load()


    }

    private fun loadInterAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this,"ca-app-pub-5717407544611949/1410986066", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialAd = interstitialAd
            }
        })

        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.d(MotionEffect.TAG, "Ad was clicked.")
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                Log.d(MotionEffect.TAG, "Ad dismissed fullscreen content.")
                mInterstitialAd = null
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                // Called when ad fails to show.
                Log.e(MotionEffect.TAG, "Ad failed to show fullscreen content.")
                mInterstitialAd = null
            }

            override fun onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d(MotionEffect.TAG, "Ad recorded an impression.")
            }

            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d(MotionEffect.TAG, "Ad showed fullscreen content.")
            }
        }

        if (mInterstitialAd != null) {
            mInterstitialAd?.show(this)
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.")
        }
    }


    private fun loadBannerAd() {
        MobileAds.initialize(this) {}

        mAdView = binding.adView
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        mAdView.adListener = object : AdListener() {
            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
                Toast.makeText(this@PdfActivity, "Returned to the app", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                // Code to be executed when an ad request fails.
            }

            override fun onAdImpression() {
                // Code to be executed when an impression is recorded
                // for an ad.
            }

            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Toast.makeText(this@PdfActivity, "Ad Loaded", Toast.LENGTH_SHORT).show()
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }
        }
    }


    private fun btnBackListener() {
        binding.back.setOnClickListener() {
            if (mInterstitialAd != null) {
                mInterstitialAd?.fullScreenContentCallback = object :
                    FullScreenContentCallback() {

                    override fun onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent()
                        val intent = Intent(this@PdfActivity,  BookActivity::class.java)
                        startActivity(intent)
                    }

                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                        super.onAdFailedToShowFullScreenContent(p0)
                    }

                    override fun onAdImpression() {
                        super.onAdImpression()
                    }

                    override fun onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent()
                    }

                }
                mInterstitialAd?.show(this)


            }else{
                val intent = Intent(this,  BookActivity::class.java)
                startActivity(intent)
            }
        }

    }

}





