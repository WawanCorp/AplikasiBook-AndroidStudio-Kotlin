package com.aplikasi.book

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.viewpager2.widget.ViewPager2
import com.aplikasi.book.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
private val  introSliderAdapter = IntroSliderAdapter (
    listOf(
        IntroSlide(
            "Belajar",
            " Jangan pernah berhenti belajar, karena hidup tak pernah berhenti mengajarkan",
            R.drawable.asset3
        ),
        IntroSlide(
            "Gemar Membaca",
            "Buku adalah teman yang berharga. Namun, sulit untuk menjelaskan hal itu kepada yang tak suka membaca.",
            R.drawable.asset10
        ),
        IntroSlide(
            "Waktu Terus Berputar",
            "Sukses hanya bisa diraih melalui gigih belajar, kerja keras, dan doa yang ikhlas. Bukan hanya dengan lamunan.",
            R.drawable.asset8
        )
    )
)

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()


        binding.introSliderViewPager.adapter = introSliderAdapter
        setupIndicators()
        setCurrentIndicator(0)

        binding.introSliderViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })
        binding.buttonNext.setOnClickListener {
            if (binding.introSliderViewPager.currentItem + 1 < introSliderAdapter.itemCount) {
                binding.introSliderViewPager.currentItem += 1
            } else {
                Intent(applicationContext, LoginActivity::class.java).also {
                    startActivity(it)
                    finish()
                }
            }
        }
    }

        private fun setupIndicators() {
            val indicator = arrayOfNulls<ImageView>(introSliderAdapter.itemCount)
            val layoutParams: LinearLayout.LayoutParams =
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            layoutParams.setMargins(8, 0, 8, 0)
            for (i in indicator.indices) {
                indicator[i] = ImageView(applicationContext)
                indicator[i].apply {
                    this?.setImageDrawable(
                        ContextCompat.getDrawable(
                            applicationContext,
                            R.drawable.indicator_inactive
                        )
                    )
                    this?.layoutParams = layoutParams
                }
                binding.indicatorContainer.addView(indicator[i])
            }
        }

        private fun setCurrentIndicator(index: Int) {

            val childCount = binding.indicatorContainer.childCount
            for (i in 0 until childCount) {
                val imageView = binding.indicatorContainer[i] as ImageView
                if (i == index) {
                    imageView.setImageDrawable(
                        ContextCompat.getDrawable(
                            applicationContext,
                            R.drawable.indicator_active
                        )
                    )
                } else {
                    imageView.setImageDrawable(
                        ContextCompat.getDrawable(
                            applicationContext,
                            R.drawable.indicator_inactive
                        )
                    )
                }
            }
        }
    }
