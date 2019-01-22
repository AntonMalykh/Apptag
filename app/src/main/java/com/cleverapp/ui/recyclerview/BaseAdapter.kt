package com.cleverapp.ui.recyclerview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<Item>: RecyclerView.Adapter<BaseViewHolder<Item>>() {

    private var items: MutableList<Item> = mutableListOf()
    private val isEmpty: MutableLiveData<Boolean> = MutableLiveData()

    protected val holderTouchCallback: ItemTouchHelper.Callback? by lazy { createTouchHelper() }

    protected open fun createTouchHelper(): ItemTouchHelper.Callback? {
        return null
    }

    fun setItems(items: List<Item>) {
        this.items = items.toMutableList()
        notifyDataSetChanged()
    }

    fun getItems(): List<Item> {
        return items
    }

    fun getIsEmptyLiveData(): LiveData<Boolean> {
        return isEmpty
    }

    override fun getItemCount(): Int {
        isEmpty.value = items.isEmpty()
        return items.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<Item>, position: Int) {
        holder.bindItem(items[position])
    }

    fun getItemTouchCallback(): ItemTouchHelper.Callback? {
        return holderTouchCallback
    }
}