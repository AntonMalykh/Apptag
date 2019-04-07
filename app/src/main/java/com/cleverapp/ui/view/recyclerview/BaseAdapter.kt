package com.cleverapp.ui.view.recyclerview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<Item>: RecyclerView.Adapter<BaseViewHolder<Item>>() {

    protected var itemsList: MutableList<Item> = mutableListOf()
    open val itemTouchHelper: ItemTouchHelper? = null

    fun setItems(items: List<Item>) {
        this.itemsList = items.toMutableList()
        notifyDataSetChanged()
    }

    fun getItems(): List<Item> {
        return itemsList
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<Item>, position: Int) {
        holder.bindItem(itemsList[position])
    }

    open fun appendItems(items: List<Item>){
        val sizeBefore = itemsList.size
        itemsList.addAll(items)
        notifyItemRangeInserted(sizeBefore, items.size)
    }
}