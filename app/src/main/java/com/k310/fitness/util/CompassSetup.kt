package com.k310.fitness.util

import android.annotation.SuppressLint
import android.util.Log
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import com.k310.fitness.databinding.FragmentTrackingBinding

class CompassSetup(private val compass: Compass, private val curActivity: FragmentActivity, private val arrowView: ImageView) {
    private var currentAzimuth = 0f

    init {
        setupCompass()
    }

    private fun setupCompass() {
        val cl = compassListener
        compass.setListener(cl)
    }

    private fun adjustArrow(azimuth: Float) {
//        Log.d(
//            TAG, "will set rotation from " + currentAzimuth + " to "
//                    + azimuth
//        )
        val an: Animation = RotateAnimation(
            -currentAzimuth, -azimuth,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
            0.5f
        )
        currentAzimuth = azimuth
        an.duration = 500
        an.repeatCount = 0
        an.fillAfter = true
        arrowView.startAnimation(an)
    }

    private val compassListener: Compass.CompassListener
        private get() = object : Compass.CompassListener {
            override fun onNewAzimuth(azimuth: Float) {
                curActivity.runOnUiThread { adjustArrow(azimuth) }
            }
        }

    companion object {
        private const val TAG = "CompassActivity"
        @SuppressLint("StaticFieldLeak")
        private var binding: FragmentTrackingBinding? = null
    }
}