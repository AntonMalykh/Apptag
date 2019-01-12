package com.cleverapp.ui.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.cleverapp.R
import com.cleverapp.repository.data.ImageTag

class TagsAdapter : RecyclerView.Adapter<TagsAdapter.TagViewHolder>() {

    var items: List<ImageTag> = emptyList()
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        return TagViewHolder(parent)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        val tag = items[position]
        holder.tag.text = tag.tag
    }

    class TagViewHolder(parent: ViewGroup) :
            RecyclerView.ViewHolder(
                    LayoutInflater.from(parent.context)
                            .inflate(R.layout.tag_view_holder, parent, false)
            ) {

        val tag: TextView = itemView.findViewById(R.id.tag)
    }

}
