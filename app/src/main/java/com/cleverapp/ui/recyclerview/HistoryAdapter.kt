package com.cleverapp.ui.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cleverapp.R
import com.cleverapp.repository.data.TaggedImage
import com.cleverapp.utils.toPlainText
import com.squareup.picasso.Picasso

class HistoryAdapter
    : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    var items: List<TaggedImage> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private var onImageClickListener: OnImageClickListener? = null

    private var onMenuClickListener: OnImageMenuClickListener? = null

    fun setOnMenuClickListener(onMenuClickListener: OnImageMenuClickListener) {
        this.onMenuClickListener = onMenuClickListener
    }

    fun setOnImageClickListener(onImageClickListener: OnImageClickListener) {
        this.onImageClickListener = onImageClickListener
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        return HistoryViewHolder(parent)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val image = items[position]
        Glide.with(holder.preview)
                .load(image.previewBytes)
                .apply(RequestOptions.centerCropTransform())
                .into(holder.preview)
        holder.itemView.setOnClickListener{ onImageClickListener?.onImageClicked(image)}
        holder.itemView.setOnLongClickListener { holder.menu.callOnClick() }
        holder.tags.text = image.tags.toPlainText()
        holder.menu.setOnClickListener {
            val menu = PopupMenu(holder.menu.context, holder.menu)
            menu.inflate(R.menu.image_item_menu)
            menu.setOnMenuItemClickListener { menuItem ->
                return@setOnMenuItemClickListener when {
                    menuItem.itemId == R.id.remove -> {
                        onMenuClickListener?.onRemoveClicked(image)
                        true
                    }
                    menuItem.itemId == R.id.copy -> {
                        onMenuClickListener?.onCopyClicked(image)
                        true
                    }
                    else ->
                        false
                }
            }
            menu.show()
        }
    }

    class HistoryViewHolder(parent: ViewGroup):
            RecyclerView.ViewHolder(
                    LayoutInflater
                            .from(parent.context)
                            .inflate(R.layout.history_view_holder, parent, false)) {

        val preview: ImageView = itemView.findViewById(R.id.preview)
        val tags: TextView = itemView.findViewById(R.id.tags)
        val menu: ImageButton = itemView.findViewById(R.id.menu)
    }
}

interface OnImageMenuClickListener {
    fun onRemoveClicked(image: TaggedImage)
    fun onCopyClicked(image: TaggedImage)
}

interface OnImageClickListener {
    fun onImageClicked(image: TaggedImage)
}