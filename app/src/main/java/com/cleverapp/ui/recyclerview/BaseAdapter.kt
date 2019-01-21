package com.cleverapp.ui.recyclerview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<Item>: RecyclerView.Adapter<BaseViewHolder<Item>>() {

    open var items: MutableList<Item> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private val isEmpty: MutableLiveData<Boolean> = MutableLiveData()

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
}