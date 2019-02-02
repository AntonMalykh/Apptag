package com.cleverapp.utils

import android.graphics.Rect
import android.view.View

fun View.isHitAreaBelow(x: Int, y: Int): Boolean {
    return with(Rect()){
        this@isHitAreaBelow.getHitRect(this)
        this.contains(x, y)
    }
}