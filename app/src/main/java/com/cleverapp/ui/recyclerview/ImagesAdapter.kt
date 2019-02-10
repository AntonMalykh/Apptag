package com.cleverapp.ui.recyclerview

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.cleverapp.R
import com.cleverapp.repository.data.TaggedImage
import com.cleverapp.ui.Mode
import com.cleverapp.utils.toPlainText
import java.util.*

class ImagesAdapter: BaseAdapter<TaggedImage>() {

    private companion object {
        const val ITEM_SELECTED_SCALE = 0.8f
        const val ITEM_NORMAL_SCALE = 1f
    }

    var layoutParamsProvider: LayoutParamsProvider? = null

    private var onImageClickListener: ((TaggedImage) -> Unit)? = null
    private var onMenuClickListener: OnImageMenuClickListener? = null
    private var mode = Mode.Normal
    private val selectedImages = mutableListOf<TaggedImage>()

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

    fun setMode(mode: Mode) {
        this.mode = mode
        if (mode == Mode.Normal)
            selectedImages.clear()
    }

    fun getSelectedImages(): List<TaggedImage> {
        return selectedImages
    }

    inner class ImageViewHolder(parent: ViewGroup):
            BaseViewHolder<TaggedImage>(
                    parent, R.layout.images_view_holder) {

        private val preview: ImageView = itemView.findViewById(R.id.preview)
        private val tags: TextView = itemView.findViewById(R.id.tags)
        private val menu: ImageButton = itemView.findViewById(R.id.menu)
        private val check: View = itemView.findViewById(R.id.check)

        override fun bindItem(item: TaggedImage) {
            super.bindItem(item)
            Glide.with(preview)
                    .load(item.previewBytes)
                    .apply(RequestOptions.centerCropTransform())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(preview)
            tags.text = item.tags.toPlainText()
            menu.setOnClickListener { onMenuClicked() }
            setMode(mode,selectedImages.contains(getItem()))
        }

        fun setMode(mode: Mode) = setMode(mode, true)

        private fun setMode(mode: Mode, animateChange: Boolean) {
            val onItemClickListener: View.OnClickListener
            when (mode) {
                Mode.Normal -> {
                    onItemClickListener = View.OnClickListener {
                        onImageClickListener?.invoke(getItem())
                    }
                    check.visibility = GONE
                }
                Mode.Remove -> {
                    onItemClickListener = View.OnClickListener {
                        if (selectedImages.contains(getItem())){
                            selectedImages.remove(getItem())
                            check.isEnabled = false
                            setScale(false)
                        }
                        else {
                            selectedImages.add(getItem())
                            check.isEnabled = true
                            setScale(true)
                        }
                    }
                    check.visibility = VISIBLE
                    check.isEnabled = selectedImages.contains(getItem())
                }
            }
            setScale(selectedImages.contains(getItem()), animateChange)
            itemView.setOnClickListener(onItemClickListener)
        }

        private fun setScale(scaleInside: Boolean, animate: Boolean = true) {
            val scale = if (scaleInside) ITEM_SELECTED_SCALE else ITEM_NORMAL_SCALE
            if (animate) {
                itemView.animate()
                        .scaleX(scale)
                        .scaleY(scale)
                        .setInterpolator(FastOutSlowInInterpolator())
                        .start()
            }
            else
                itemView.apply { scaleX = scale; scaleY = scale }
        }

        private fun onMenuClicked() {
            val menu = PopupMenu(menu.context, menu)
            menu.inflate(R.menu.image_item_menu)
            menu.setOnMenuItemClickListener { menuItem ->
                return@setOnMenuItemClickListener when {
                    menuItem.itemId == R.id.remove -> {
                        onMenuClickListener?.onRemoveClicked(getItem())
                        true
                    }
                    menuItem.itemId == R.id.copy -> {
                        onMenuClickListener?.onCopyClicked(getItem())
                        true
                    }
                    else -> false
                }
            }
            menu.show()
        }
    }
}

interface OnImageMenuClickListener {
    fun onRemoveClicked(image: TaggedImage)
    fun onCopyClicked(image: TaggedImage)
}