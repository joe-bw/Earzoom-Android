package com.sorizava.asrplayer.extension

import android.app.Service
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.SeekBar
import androidx.recyclerview.widget.RecyclerView

fun View.beInvisibleIf(beInvisible: Boolean) = if (beInvisible) beInvisible() else beVisible()

fun View.beVisibleIf(beVisible: Boolean) = if (beVisible) beVisible() else beGone()

fun View.beGoneIf(beGone: Boolean) = beVisibleIf(!beGone)

fun View.beInvisible() {
    visibility = View.INVISIBLE
}

fun View.beVisible() {
    visibility = View.VISIBLE
}

fun View.beGone() {
    visibility = View.GONE
}

fun View.onGlobalLayout(callback: () -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            callback()
        }
    })
}

fun View.isVisible() = visibility == View.VISIBLE

fun View.isInvisible() = visibility == View.INVISIBLE

fun View.isGone() = visibility == View.GONE

fun View.hideKeyboard() {
    (this.context.getSystemService(Service.INPUT_METHOD_SERVICE) as? InputMethodManager)
        ?.hideSoftInputFromWindow(this.windowToken, 0)
}

fun View.showKeyboard() {
    (this.context.getSystemService(Service.INPUT_METHOD_SERVICE) as? InputMethodManager)
        ?.showSoftInput(this, 0)
}

fun SeekBar.onProgressChanged(validate: Boolean, callback: (progress: Int) -> Unit) {
    this.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            if (fromUser && validate) {
                callback(progress)
            }
        }
        override fun onStartTrackingTouch(seekBar: SeekBar) { return }
        override fun onStopTrackingTouch(seekBar: SeekBar) { return }
    })
}

/** RecyclerView 외곽 touch 처리 extension */
fun RecyclerView.onOutSideTouchListener(callback: () -> Unit) {
    this.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
        override fun onInterceptTouchEvent(
            recyclerView: RecyclerView,
            motionEvent: MotionEvent
        ): Boolean {
            return when {
                motionEvent.action != MotionEvent.ACTION_UP || recyclerView.findChildViewUnder(
                    motionEvent.x,
                    motionEvent.y
                ) != null -> false
                else -> {
                    callback()
                    true
                }
            }
        }

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        override fun onTouchEvent(recyclerView: RecyclerView, motionEvent: MotionEvent) {}
    })
}

/** 포커스 재설정 */
fun View.handleFocus() {
    this.clearFocus()
    this.isFocusable = false
    this.isFocusableInTouchMode = true
    this.isFocusable = true
}
