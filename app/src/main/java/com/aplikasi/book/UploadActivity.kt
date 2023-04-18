@file:Suppress("DEPRECATION")

package com.aplikasi.book

import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.helper.widget.MotionEffect
import com.aplikasi.book.databinding.ActivityUploadBinding
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage


@Suppress("ControlFlowWithEmptyBody")
class UploadActivity : AppCompatActivity() {

    private var mInterstitialAd: InterstitialAd? = null
    private lateinit var mAdView: AdView

    private var  title = ""
    private var  decription = ""

    private lateinit var binding: ActivityUploadBinding

    private lateinit var  firebaseAuth: FirebaseAuth

    private lateinit var progressDialog: ProgressDialog

    private lateinit var mAuth: FirebaseAuth


    private var pdfUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        loadBannerAd()
        loadInterAd()

        mAuth = FirebaseAuth.getInstance()

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.backBtn.setOnClickListener{
            if (mInterstitialAd != null) {
                mInterstitialAd?.fullScreenContentCallback = object :
                    FullScreenContentCallback() {

                    override fun onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent()
                        val intent = Intent(this@UploadActivity, ProfileActivity::class.java)
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


            } else {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }
        }


        binding.btnSelectFile.setOnClickListener {
            pdfPickIntent()
        }
        binding.btnUploadFile.setOnClickListener {
            validateData()
        }
    }

    private fun loadInterAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            "ca-app-pub-5717407544611949/1410986066",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                }
            })

        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
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
                Toast.makeText(this@UploadActivity, "Returned to the app", Toast.LENGTH_SHORT)
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
                Toast.makeText(this@UploadActivity, "Ad Loaded", Toast.LENGTH_SHORT).show()
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }
        }
    }


    private fun validateData() {
        //step1: validate data
        Log.d(TAG,"validateData: validating data")

        //get data
        title = binding.titleEt.text.toString().trim()
        decription = binding.descriptionEt.text.toString().trim()

        //validate data
        if (title.isEmpty()){
            Toast.makeText(this, "Enter Title....", Toast.LENGTH_SHORT).show()

        }
        else if (decription.isEmpty()){
            Toast.makeText(this, "Enter Description....", Toast.LENGTH_SHORT).show()

        }
        else if ( pdfUri == null){
            Toast.makeText(this, "Enter Description....", Toast.LENGTH_SHORT).show()

        }
        else{
            //data validate
            uploadPdfToStronge()
        }

    }

    private fun uploadPdfToStronge() {
        Log.d(TAG, "uploadPdfToStronge: uploading to storage")

        //show progress dialog
        progressDialog.setMessage("Uploading PDF...")
        progressDialog.show()

        //timestamp
        val  timestamp = System.currentTimeMillis()

        //path of pdf in firebase storage
        val filePathAndName = "Book/$timestamp"

        //storage referance
        val storageReference = FirebaseStorage.getInstance().getReference(filePathAndName)
        storageReference.putFile(pdfUri!!)
            .addOnSuccessListener { taskSnapshot ->
                Log.d(TAG, "uploadPdfToStronge: PDF uploaded now getting url")
                //step 3 Get url of uploaded pdf
                val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val uploadedPdfUrl = "${uriTask.result}"

                uploadpdfInfoToDb(uploadedPdfUrl, timestamp)
            }
            .addOnFailureListener{e ->
                Log.d(TAG, "uploadPdfToStronge: failed to upload due to ${e.message}")
                progressDialog.dismiss()
                Toast.makeText(this, "Failed to upload due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadpdfInfoToDb(uploadedPdfUrl: String, timestamp: Long) {
        Log.d(TAG, "uploadpdfInfoToDb: uploading to db")
        progressDialog.setMessage("Uploading pdf info")

        //uid of current user
        val uid = firebaseAuth.uid

        val user = Firebase.auth.currentUser
        if (user != null) {
            // User is signed in
        } else {
            // No user is signed in
        }


        //setup data to upload
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["uid"] = "$uid"
        hashMap["id"] = "$timestamp"
        hashMap["title"] = title
        hashMap["decription"] = decription
        hashMap["url"] = uploadedPdfUrl
        hashMap["timestamp"] = timestamp
        hashMap["viewCount"] = 0
        hashMap["downloadsCount"] = 0


        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                Log.d(TAG, "uploadpdfInfoToDb:  uploaded to db")
                progressDialog.dismiss()
                Toast.makeText(this, "uploaded....", Toast.LENGTH_SHORT).show()
                pdfUri = null
            }
            .addOnFailureListener{ e->
                Log.d(TAG, "uploadpdfInfoToDb: failed to upload due to ${e.message}")
                progressDialog.dismiss()
                Toast.makeText(this, "Failed to upload due to ${e.message}", Toast.LENGTH_SHORT).show()
            }



    }

    private fun pdfPickIntent() {
        Log.d(TAG, "pdfPickIntent:  starting pdf pick intent")

        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        pdfActivityResultLauncher.launch(intent)

    }

    private val pdfActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            Log.d(TAG, "PDF Picked: ")
            pdfUri = result.data!!.data
        } else {
            Log.d(TAG, "PDF Pick cancelled ")
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

}





