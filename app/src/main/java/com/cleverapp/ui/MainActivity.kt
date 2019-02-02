package com.cleverapp.ui

import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.fragment.NavHostFragment
import com.cleverapp.R

class MainActivity : AppCompatActivity(), NavHost {

    private val navigationController: NavController by lazy {
        (supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                as NavHostFragment)
                .navController
    }

    override fun getNavController() = navigationController

    override fun onBackPressed() {
        val topFragment = getTopFragment()
        if (topFragment !is BaseFragment || !topFragment.onBackPressed())
            super.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
