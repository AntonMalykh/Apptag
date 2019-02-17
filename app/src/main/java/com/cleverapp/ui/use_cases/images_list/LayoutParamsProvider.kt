package com.cleverapp.ui.use_cases.images_list

import android.view.ViewGroup

class LayoutParamsProvider(
        private val windowWidth: Int,
        var viewMode: HistoryViewMode) {

    fun getLayoutParams(): ViewGroup.LayoutParams {
        val itemWidth = windowWidth / viewMode.spanCount
        return ViewGroup.LayoutParams(itemWidth, itemWidth)
    }
}
