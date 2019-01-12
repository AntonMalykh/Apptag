package com.cleverapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavHost
import com.cleverapp.App
import com.cleverapp.R

class MainActivity: AppCompatActivity(), NavHost {

    override fun getNavController(): NavController {
        return NavController(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
