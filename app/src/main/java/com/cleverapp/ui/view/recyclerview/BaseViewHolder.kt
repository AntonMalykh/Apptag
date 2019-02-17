package com.cleverapp.ui.view.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView


abstract class BaseViewHolder<T>(
        parent: ViewGroup,
        @LayoutRes layoutId: Int)
    : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
                .inflate(layoutId, parent, false)){

    private var item: T? = null

    open fun bindItem(item: T) {
        this.item = item
    }

    protected fun getItem() = item!!
}