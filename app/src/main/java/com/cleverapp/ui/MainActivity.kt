package com.cleverapp.ui

import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View.GONE
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.fragment.NavHostFragment
import com.cleverapp.BuildConfig
import com.cleverapp.R
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView

class MainActivity : AppCompatActivity(), NavHost {

    private val navigationController: NavController by lazy {
        (supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                as NavHostFragment)
                .navController
    }
    private lateinit var adView: AdView

    override fun getNavController() = navigationController

    override fun onBackPressed() {
        val topFragment = getTopFragment()
        if (topFragment !is BaseFragment || !topFragment.onBackPressed())
            super.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adView = findViewById(R.id.adView)

        if (!"adFree".equals(BuildConfig.FLAVOR)) {
            adView.loadAd(
                    AdRequest
                            .Builder()
                            .build()
            )
        }
        else
            adView.visibility = GONE
    }

    override fun onResume() {
        super.onResume()
        adView.resume()
    }

    override fun onPause() {
        adView.pause()
        super.onPause()
    }

    override fun onDestroy() {
        adView.destroy()
        super.onDestroy()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val topFragment = getTopFragment()
        if (topFragment is BaseFragment)
            topFragment.onTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }

    private fun getTopFragment(): Fragment? {
        return supportFragmentManager
                .primaryNavigationFragment
                ?.childFragmentManager!!
                .fragments.last()
    }
}
