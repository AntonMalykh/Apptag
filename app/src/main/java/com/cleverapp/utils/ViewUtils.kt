package com.cleverapp.utils

import android.graphics.Rect
import android.view.View

fun View.isVisibleAreaContains(x: Int, y: Int): Boolean {
    return with(Rect()){
        this@isVisibleAreaContains.getGlobalVisibleRect(this)
        this.contains(x, y)
    }
}