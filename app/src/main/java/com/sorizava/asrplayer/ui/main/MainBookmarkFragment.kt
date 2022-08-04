/*
 * Create by jhong on 2022. 6. 22.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.ui.main

import android.annotation.SuppressLint
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.sorizava.asrplayer.data.item.BookmarkItem
import com.sorizava.asrplayer.extension.observe
import com.sorizava.asrplayer.extension.onOutSideTouchListener
import com.sorizava.asrplayer.extension.toast
import com.sorizava.asrplayer.ui.base.BaseFragment
import org.mozilla.focus.R
import org.mozilla.focus.databinding.FragmentMainBookmarkBinding

class MainBookmarkFragment : BaseFragment<FragmentMainBookmarkBinding>(
    FragmentMainBookmarkBinding::inflate
), BookmarkItemSelectedListener<BookmarkItem> {

    private val viewModel: MainBookmarkViewModel by viewModels()

    private lateinit var bookmarkAdapter: MainBookmarkAdapter

    private var listener: OnSwipeHandleListener? = null

    companion object {
        fun newInstance(listener: OnSwipeHandleListener) =
            MainBookmarkFragment().apply {
                this.listener = listener
            }
    }

    override fun initView() {
        setupUI()
        requestBookmarkList()
    }

    private fun setupUI() {


        binding.listBookmark.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener{
            override fun onInterceptTouchEvent(recyclerView: RecyclerView, motionEvent: MotionEvent): Boolean {
                if (motionEvent.action != MotionEvent.ACTION_UP) {
                    return false
                }
                val child: View? =
                    recyclerView.findChildViewUnder(motionEvent.x, motionEvent.y)
                return if (child != null) {
                    activity?.toast("CHILD NOT")
                    false
                } else {
                    activity?.toast("CHILD")
                    true
                }
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}

        })

        // 편집 모드 상태일 경우 icon 외곽을 누르면 편집 모드 해제 되도록 처리
//        binding.listBookmark.onOutSideTouchListener {
//            offEditMode()
//        }
    }

    private fun offEditMode() {
        listener?.onStopViewPagerSwipe(false)
        bookmarkAdapter.setEditMode(false)
        viewModel.isEditMode = false
    }

    private fun requestBookmarkList() {
        viewModel.requestBookmarkList()
    }

    override fun initViewModelObserver() {
        observe(viewModel.bookmarkList, ::handleList)
    }

    private fun handleList(resultState: ArrayList<BookmarkItem>) {
        bindListData(list = resultState)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun bindListData(list: ArrayList<BookmarkItem>) {
        val animation = AnimationUtils.loadAnimation(context, R.anim.wobble_item)
        bookmarkAdapter = MainBookmarkAdapter(list, this, animation)
        binding.listBookmark.adapter = bookmarkAdapter

        val callback = ItemTouchHelperCallback(bookmarkAdapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(binding.listBookmark)
        bookmarkAdapter.startDrag(object : MainBookmarkAdapter.OnStartDragListener {
            override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
                touchHelper.startDrag(viewHolder)
            }
        })
    }

    private fun handleTest(s: String?) {
        Log.e("TEST", "TEST: $s")
    }

    override fun onSelectedItem(item: BookmarkItem, mode: Boolean) {

        when(mode) {
            true -> {

            }
            false -> {

            }
        }
        if(item.isAdd) {
            activity?.toast("isADD")
        } else {
            activity?.toast(item.name)
        }

    }

    override fun onLongSelectedItem(item: BookmarkItem, mode: Boolean) {

        viewModel.isEditMode = mode
        listener?.onStopViewPagerSwipe(true)
        if(!item.isAdd) {

//            val dialog: DialogFragment = EditBookmarkDialog(object:
//                EditBookmarkDialog.EditBookmarkDialogListener {
//                override fun onDialogDeleteClick(dialog: DialogFragment) {
//                    dialog.dismiss()
//                    deleteItem()
//                }
//
//                override fun onDialogChangeNameClick(dialog: DialogFragment) {
//                    dialog.dismiss()
//                    showChangeNameDialog()
//                }
//
//            })
//            dialog.show(activity?.supportFragmentManager!!, "EditBookmarkDialog")
        }
    }

    private fun showChangeNameDialog() {
        val dialog: DialogFragment = ChangeNameBookmarkDialog(object:
            ChangeNameBookmarkDialog.ChangeNameBookmarkDialogListener {
            override fun onDialogYesClick(dialog: DialogFragment, name: String) {
                dialog.dismiss()
                changeItemName(name)
            }

            override fun onDialogNoClick(dialog: DialogFragment) {
                dialog.dismiss()
            }

        })
        dialog.show(activity?.supportFragmentManager!!, "EditBookmarkDialog")
    }

    private fun changeItemName(name: String) {
        activity?.toast("$name 으로 이름 변경 예정")
    }

    private fun deleteItem() {
        activity?.toast("아이템 삭제 예정")
    }
}


