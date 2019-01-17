package com.cleverapp.ui.recyclerview

import android.view.ViewGroup

class LayoutParamsProvider(
        private val windowWidth: Int,
        private val spans: Int) {

    fun getLayoutParams(): ViewGroup.LayoutParams {
        val itemWidth = windowWidth / spans
        return ViewGroup.LayoutParams(itemWidth, itemWidth)
    }
}
