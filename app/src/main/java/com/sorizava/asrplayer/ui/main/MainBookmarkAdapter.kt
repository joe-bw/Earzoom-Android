/*
 * Create by jhong on 2022. 6. 3.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.ui.main

import android.R
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.AnimationUtils.loadAnimation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sorizava.asrplayer.data.item.BookmarkItem
import com.sorizava.asrplayer.ui.base.ItemSelectedListener
import org.mozilla.focus.databinding.AdapterBookmarkItemBinding
import java.util.*

class MainBookmarkAdapter(
    private val boardItemList: ArrayList<BookmarkItem>,
    private val listener: BookmarkItemSelectedListener<BookmarkItem>,
    private var animation: Animation
) : RecyclerView.Adapter<MainBookmarkAdapter.BookmarkViewHolder>(),
    ItemTouchHelperCallback.OnItemMoveListener {

    private var isEditMode: Boolean = false

    private lateinit var dragListener: OnStartDragListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {

        val itemBinding =
            AdapterBookmarkItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookmarkViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        holder.bind(boardItemList[position], listener)
    }

    override fun getItemCount(): Int {
        return boardItemList.size
    }

    fun setEditMode(mode: Boolean) {
        this.isEditMode = mode
        notifyDataSetChanged()
    }

    private fun isEditMode(): Boolean {
        return this.isEditMode
    }

    inner class BookmarkViewHolder(
        private val itemBinding: AdapterBookmarkItemBinding,
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        @SuppressLint("ClickableViewAccessibility")
        fun bind(item: BookmarkItem, listener: BookmarkItemSelectedListener<BookmarkItem>) {
            itemBinding.apply {
                Glide.with(imgLogo.rootView)
                    .load(item.img)
                    .centerInside()
                    .into(imgLogo)
                txtName.text = item.name

                // 편집 모드 상태 전환
                if (isEditMode()) {
                    layoutItem.startAnimation(animation)
                } else {
                    layoutItem.clearAnimation()
                }

                // 모드 상태에 따른 이벤트 처리
                if (isEditMode()) {
                    /** 마지막 항목은 이동할 수 없도록 처리 */
                    if (!item.isAdd) {
                        layoutItem.setOnLongClickListener {
                            dragListener.onStartDrag(this@BookmarkViewHolder)
                            true
                        }
                    }
                } else {
                    layoutItem.setOnLongClickListener {
                        setEditMode(true)
                        listener.onLongSelectedItem(item, true)
                        true
                    }
                }

                layoutItem.setOnClickListener {
                    listener.onSelectedItem(item, isEditMode())
                }
            }
        }
    }

    interface OnStartDragListener {
        fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
    }

    fun startDrag(listener: OnStartDragListener) {
        this.dragListener = listener
    }

    /** 마지막 항목으로 이동할 수 없게 처리 */
    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        if (toPosition == (itemCount - 1)) return
        Collections.swap(boardItemList, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemSwiped(position: Int) {
        boardItemList.removeAt(position)
        notifyItemRemoved(position)
    }
}
