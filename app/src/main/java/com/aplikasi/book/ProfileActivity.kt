package com.aplikasi.book

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.helper.widget.MotionEffect.TAG
import com.aplikasi.book.databinding.ActivityProfileBinding
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

@Suppress("DEPRECATION")
class ProfileActivity : AppCompatActivity() {

    private  var  backPressedTime = 0L
    private lateinit var auth: FirebaseAuth

    private var mInterstitialAd: InterstitialAd? = null

    private lateinit var mAdView: AdView

    private lateinit var binding: ActivityProfileBinding
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        uploadFile()
        gotoBook()

        loadBannerAd()


        loadInterAd()

//        setting()

        binding.switch1.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

            }


        }

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user != null) {
            if (user.photoUrl != null) {
                Picasso.get().load(user.photoUrl).into(binding.imgProfile)

            } else {
                Picasso.get().load("https://bit.ly/3wc3Lcn").into(binding.imgProfile)
            }

            binding.txtNama.text = user.displayName
            binding.txtEmail.text = user.email

            binding.btnout.setOnClickListener {
                Firebase.auth.signOut()

                if (mInterstitialAd != null) {
                    mInterstitialAd?.fullScreenContentCallback = object :
                        FullScreenContentCallback() {

                        override fun onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent()
                            val intent = Intent(this@ProfileActivity,  LoginActivity::class.java)
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
                  val intent = Intent(this,  LoginActivity::class.java)
                    startActivity(intent)
                }
            }

        }
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
                Log.d(TAG, "Ad was clicked.")
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                Log.d(TAG, "Ad dismissed fullscreen content.")
                mInterstitialAd = null
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                // Called when ad fails to show.
                Log.e(TAG, "Ad failed to show fullscreen content.")
                mInterstitialAd = null
            }

            override fun onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d(TAG, "Ad recorded an impression.")
            }

            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d(TAG, "Ad showed fullscreen content.")
            }
        }

        if (mInterstitialAd != null) {
            mInterstitialAd?.show(this)
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.")
        }
    }


//    private fun setting() {
//        binding.setting.setOnClickListener {
//            startActivity(Intent(this, SettingActivity::class.java))
//        }
//    }

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
                Toast.makeText(this@ProfileActivity, "Returned to the app", Toast.LENGTH_SHORT)
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
                Toast.makeText(this@ProfileActivity, "Ad Loaded", Toast.LENGTH_SHORT).show()
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }
        }
    }



    private fun gotoBook() {
        binding.card1.setOnClickListener {
            startActivity(Intent(this, BookActivity::class.java))
        }
    }

    private fun uploadFile() {
        binding.card2.setOnClickListener {
            startActivity(Intent(this, UploadActivity::class.java))
        }
    }




    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
        }else{
            Toast.makeText(applicationContext, "Press back again to exit app", Toast.LENGTH_SHORT).show()
        }

        backPressedTime = System.currentTimeMillis()
    }
}

