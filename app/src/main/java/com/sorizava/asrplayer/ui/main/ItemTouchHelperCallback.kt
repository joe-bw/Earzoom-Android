/*
 * Create by jhong on 2022. 7. 29.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.ui.main

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class ItemTouchHelperCallback(
    private val itemMoveListener: OnItemMoveListener
) : ItemTouchHelper.Callback() {

    interface OnItemMoveListener {
        fun onItemMoved(fromPosition: Int, toPosition: Int)
        fun onItemSwiped(position: Int)
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return if (recyclerView.layoutManager is GridLayoutManager) {
            // GridLayout 형식인 경우
            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            val swipeFlags = 0
            makeMovementFlags(dragFlags, swipeFlags)
        } else {
            // 일반 LinearLayout 형식인 경우
            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
            makeMovementFlags(dragFlags, swipeFlags)
        }
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
//        itemMoveListener.onItemMoved(viewHolder.adapterPosition, target.adapterPosition)
        itemMoveListener.onItemMoved(viewHolder.bindingAdapterPosition, target.bindingAdapterPosition)
        return true
    }


    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        itemMoveListener.onItemSwiped(viewHolder.bindingAdapterPosition)
    }

    override fun isLongPressDragEnabled(): Boolean = false
}