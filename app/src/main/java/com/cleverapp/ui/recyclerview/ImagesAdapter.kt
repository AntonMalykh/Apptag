package com.cleverapp.ui.recyclerview

import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.cleverapp.R
import com.cleverapp.repository.data.TaggedImage
import com.cleverapp.utils.toPlainText
import java.util.*

class ImagesAdapter: BaseAdapter<TaggedImage>() {

    private var onImageClickListener: ((TaggedImage) -> Unit)? = null
    private var onMenuClickListener: OnImageMenuClickListener? = null

    var layoutParamsProvider: LayoutParamsProvider? = null

    override val itemTouchHelper = ItemTouchHelper(
            object: ItemTouchHelper.Callback(){
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    // not supported
                }

                override fun getMovementFlags(recyclerView: RecyclerView,
                                              viewHolder: RecyclerView.ViewHolder): Int {
                    return makeFlag(
                            ItemTouchHelper.ACTION_STATE_DRAG,
                            ItemTouchHelper.DOWN
                                    or ItemTouchHelper.UP
                                    or ItemTouchHelper.START
                                    or ItemTouchHelper.END)
                }

                override fun onMove(recyclerView: RecyclerView,
                                    viewHolder: RecyclerView.ViewHolder,
                                    target: RecyclerView.ViewHolder): Boolean {
                    Collections.swap(getItems(), viewHolder.adapterPosition, target.adapterPosition)
                    notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
                    return true
                }
    })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<TaggedImage> {
        val holder = ImageViewHolder(parent)
        layoutParamsProvider?.let {
            holder.itemView.layoutParams = it.getLayoutParams()
        }
        return holder
    }

    fun setOnMenuClickListener(onMenuClickListener: OnImageMenuClickListener) {
        this.onMenuClickListener = onMenuClickListener
    }

    fun setOnImageClickListener(listener: (TaggedImage) -> Unit) {
        this.onImageClickListener = listener
    }

    inner class ImageViewHolder(parent: ViewGroup):
            BaseViewHolder<TaggedImage>(
                    parent, R.layout.history_view_holder) {

        private val preview: ImageView = itemView.findViewById(R.id.preview)
        private val tags: TextView = itemView.findViewById(R.id.tags)
        private val menu: ImageButton = itemView.findViewById(R.id.menu)

        override fun bindItem(item: TaggedImage) {
            super.bindItem(item)
            Glide.with(preview)
                    .load(item.previewBytes)
                    .apply(RequestOptions.centerCropTransform())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(preview)
            itemView.setOnClickListener{ onImageClickListener?.invoke(item) }
            tags.text = item.tags.toPlainText()
            menu.setOnClickListener {
                val menu = PopupMenu(menu.context, menu)
                menu.inflate(R.menu.image_item_menu)
                menu.setOnMenuItemClickListener { menuItem ->
                    return@setOnMenuItemClickListener when {
                        menuItem.itemId == R.id.remove -> {
                            onMenuClickListener?.onRemoveClicked(item)
                            true
                        }
                        menuItem.itemId == R.id.copy -> {
                            onMenuClickListener?.onCopyClicked(item)
                            true
                        }
                        else ->
                            false
                    }
                }
                menu.show()
            }
        }
    }
}

interface OnImageMenuClickListener {
    fun onRemoveClicked(image: TaggedImage)
    fun onCopyClicked(image: TaggedImage)
}