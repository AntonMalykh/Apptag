package com.cleverapp.ui.recyclerview

import android.view.ViewGroup
import com.cleverapp.ui.viewmodels.HistoryViewMode

class LayoutParamsProvider(
        private val windowWidth: Int,
        var viewMode: HistoryViewMode) {

    fun getLayoutParams(): ViewGroup.LayoutParams {
        val itemWidth = windowWidth / viewMode.spanCount
        return ViewGroup.LayoutParams(itemWidth, itemWidth)
    }
}
