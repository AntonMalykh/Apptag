package com.cleverapp.ui.recyclerview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<Item>: RecyclerView.Adapter<BaseViewHolder<Item>>() {

    protected var itemsList: MutableList<Item> = mutableListOf()
    private val isEmpty: MutableLiveData<Boolean> = MutableLiveData()
    open val itemTouchHelper: ItemTouchHelper? = null

    fun setItems(items: List<Item>) {
        this.itemsList = items.toMutableList()
        notifyDataSetChanged()
    }

    fun getItems(): List<Item> {
        return itemsList
    }

    fun getIsEmptyLiveData(): LiveData<Boolean> {
        return isEmpty
    }

    override fun getItemCount(): Int {
        isEmpty.value = itemsList.isEmpty()
        return itemsList.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<Item>, position: Int) {
        holder.bindItem(itemsList[position])
    }
}