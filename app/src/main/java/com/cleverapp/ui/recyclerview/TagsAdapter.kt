package com.cleverapp.ui.recyclerview

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.REVERSE
import android.graphics.Color
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.cleverapp.R
import com.cleverapp.repository.data.ImageTag
import java.util.*


class TagsAdapter: BaseAdapter<ImageTag>() {

    private var onTagRemovedCallback: ((ImageTag) -> Unit)? = null
    private var onEditTagClickedCallback: ((ImageTag) -> Unit)? = null

    override val itemTouchHelper: ItemTouchHelper = ItemTouchHelper(
            object: ItemTouchHelper.Callback(){
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                }

                override fun getMovementFlags(recyclerView: RecyclerView,
                                              viewHolder: RecyclerView.ViewHolder): Int {
                    return makeMovementFlags(
                            ItemTouchHelper.DOWN
                                    or ItemTouchHelper.UP,
                            ItemTouchHelper.RIGHT)
                }

                override fun onMove(recyclerView: RecyclerView,
                                    viewHolder: RecyclerView.ViewHolder,
                                    target: RecyclerView.ViewHolder): Boolean {
                    Collections.swap(getItems(), viewHolder.adapterPosition, target.adapterPosition)
                    notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
                    return true
                }

                override fun isLongPressDragEnabled(): Boolean = false
            })

    fun setOnTagRemovedCallback(callback:(ImageTag) -> Unit) {
        onTagRemovedCallback = callback
    }

    fun setOnEditTagClickedCallback(callback: (ImageTag) -> Unit) {
        onEditTagClickedCallback = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ImageTag> {
        return TagViewHolder(parent)
    }

    fun updateTag(tag: ImageTag) {
        val position = itemsList.indexOf(tag)
        if (position >= 0) {
            tag.isCustom = true
            notifyItemChanged(position)
        }
        else
            itemsList.add(0, tag)
    }

    inner class TagViewHolder(parent: ViewGroup):
            BaseViewHolder<ImageTag>(
                    parent,
                    R.layout.tag_view_holder) {

        private val tag: TextView = itemView.findViewById(R.id.tag)
        private val edit: View = itemView.findViewById(R.id.edit)
        private val move: View = itemView.findViewById(R.id.move)

        override fun bindItem(item: ImageTag) {
            tag.text = item.tag
            edit.setOnClickListener {
                onEditTagClickedCallback?.invoke(item)
            }
            move.setOnTouchListener {
                _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {

                    val colorAnimation = ValueAnimator.ofObject(
                            ArgbEvaluator(),
                            Color.TRANSPARENT,
                            ResourcesCompat.getColor(
                                    itemView.context.resources,
                                    R.color.colorAccent_transparent,
                                    null))
                    colorAnimation.duration = 300 // milliseconds
                    colorAnimation.repeatMode = REVERSE
                    colorAnimation.repeatCount = 1
                    colorAnimation.addUpdateListener {
                        animator -> itemView.setBackgroundColor(animator.animatedValue as Int) }
                    colorAnimation.start()
                    itemTouchHelper.startDrag(this)
                }
                true
            }
        }
    }
}
