package com.cleverapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.fragment.NavHostFragment
import com.cleverapp.R

class MainActivity: AppCompatActivity(), NavHost {

    private val navigationController: NavController by lazy {
        (supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                as NavHostFragment)
                .navController
    }

    override fun getNavController() = navigationController

    override fun onBackPressed() {
        val topFragment = supportFragmentManager
                .primaryNavigationFragment
                ?.childFragmentManager!!
                .fragments.last()
        if (topFragment !is BaseFragment || !topFragment.onBackPressed())
            super.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
