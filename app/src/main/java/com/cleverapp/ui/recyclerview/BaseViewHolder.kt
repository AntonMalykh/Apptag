package com.cleverapp.ui.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView


abstract class BaseViewHolder(
        parent: ViewGroup,
        @LayoutRes layoutId: Int)
    : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
                .inflate(layoutId, parent, false))