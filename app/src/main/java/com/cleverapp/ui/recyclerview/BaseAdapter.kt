package com.cleverapp.ui.recyclerview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<Item, Holder: BaseViewHolder>: RecyclerView.Adapter<Holder>() {

    var items: List<Item> = emptyList()
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

    override fun onBindViewHolder(holder: Holder, position: Int) {
        bindViewHolder(holder, items[position])
    }

    protected abstract fun bindViewHolder(holder: Holder, item: Item)
}