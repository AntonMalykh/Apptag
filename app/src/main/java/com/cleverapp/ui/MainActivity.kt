package com.cleverapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cleverapp.App
import com.cleverapp.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null)
            supportFragmentManager.beginTransaction().add(R.id.root, HistoryFragment()).commit()
    }
}
