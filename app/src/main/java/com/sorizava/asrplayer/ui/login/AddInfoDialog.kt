/*
 * Create by jhong on 2022. 7. 7.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */
package com.sorizava.asrplayer.ui.login

import android.view.LayoutInflater
import android.view.ViewGroup
import org.mozilla.focus.R
import android.util.DisplayMetrics
import android.widget.CheckBox
import android.widget.CompoundButton
import android.app.DatePickerDialog
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.widget.DatePicker
import android.widget.EditText
import com.google.android.material.snackbar.Snackbar
import android.view.WindowManager
import android.graphics.drawable.InsetDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.sorizava.asrplayer.ui.login.AddInfoDialog.AddInfoDialogListener
import java.lang.ClassCastException
import java.lang.StringBuilder
import java.util.*

class AddInfoDialog : DialogFragment() {

    interface AddInfoDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment?, date: String?, birth: String?)
        fun onDialogNegativeClick(dialog: DialogFragment?)
        fun onDialogPrivacyClick(dialog: DialogFragment?)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_add_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val displayMetrics = requireContext().resources.displayMetrics
        dialog!!.window!!.setLayout(displayMetrics.widthPixels, displayMetrics.heightPixels)

//        ((EditText)view.findViewById(R.id.edit_phone)).setText("010");
        val checkBox = view.findViewById<View>(R.id.chk_privacy) as CheckBox
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkBox.setTextColor(Color.WHITE)
                checkBox.setError("", null)
            }
        }
        view.findViewById<View>(R.id.btn_privacy)
            .setOnClickListener { listener!!.onDialogPrivacyClick(this@AddInfoDialog) }
        view.findViewById<View>(R.id.btn_birth).setOnClickListener { v ->
//            val calendar = Calendar.getInstance()
            val pickerDialog = DatePickerDialog(
                requireActivity(),
                android.R.style.Theme,
                { _, year, month, dayOfMonth ->
                    (view.findViewById<View>(R.id.edit_year) as EditText).setText("" + year)
                    (view.findViewById<View>(R.id.edit_month) as EditText).setText("" + (month + 1))
                    (view.findViewById<View>(R.id.edit_day) as EditText).setText("" + dayOfMonth)
                    v.visibility = View.GONE
                    view.findViewById<View>(R.id.layout_birth).visibility = View.VISIBLE
                },
                2000,
                0,
                1
            )
            pickerDialog.show()
        }
        view.findViewById<View>(R.id.btn_cancel)
            .setOnClickListener { listener!!.onDialogNegativeClick(this@AddInfoDialog) }
        view.findViewById<View>(R.id.btn_confirm).setOnClickListener(View.OnClickListener {
            if (!checkBox.isChecked) {
                checkBox.error = "동의 필요"
                checkBox.setTextColor(Color.RED)
                checkBox.isFocusable = true
                return@OnClickListener
            }
            if (isValid(view)) {
                listener!!.onDialogPositiveClick(
                    this@AddInfoDialog,
                    getDateTime(view),
                    getPhoneValue(view)
                )
            } else {
                Snackbar.make(view, getString(R.string.txt_auth_need), Snackbar.LENGTH_SHORT).show()
            }
        })
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        val lpWindow = WindowManager.LayoutParams()
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        lpWindow.dimAmount = 0.8f
        dialog.window!!.attributes = lpWindow
        val insetDrawable = InsetDrawable(ColorDrawable(Color.TRANSPARENT), 0)
        dialog.window!!.setBackgroundDrawable(insetDrawable)
        return dialog
    }

    private fun isValid(view: View): Boolean {
        val edit_year = view.findViewById<EditText>(R.id.edit_year)
        val yearValue = edit_year.text.toString().trim { it <= ' ' }
        if (yearValue.isEmpty()) return false
        val edit_month = view.findViewById<EditText>(R.id.edit_month)
        val monthValue = edit_month.text.toString().trim { it <= ' ' }
        if (monthValue.isEmpty()) return false
        val edit_day = view.findViewById<EditText>(R.id.edit_day)
        val dayValue = edit_day.text.toString().trim { it <= ' ' }
        if (dayValue.isEmpty()) return false
        val edit_phone = view.findViewById<EditText>(R.id.edit_phone)
        val phoneValue = edit_phone.text.toString().trim { it <= ' ' }
        return if (phoneValue.isEmpty()) false else true
    }

    fun getPhoneValue(view: View): String {
        val edit_phone = view.findViewById<EditText>(R.id.edit_phone)
        return edit_phone.text.toString().trim { it <= ' ' }
    }

    fun getDateTime(view: View): String {
        val edit_year = view.findViewById<EditText>(R.id.edit_year)
        val yearValue = edit_year.text.toString().trim { it <= ' ' }
        val edit_month = view.findViewById<EditText>(R.id.edit_month)
        var monthValue = edit_month.text.toString().trim { it <= ' ' }
        if (monthValue.length == 1) {
            monthValue = "0$monthValue"
        }
        val edit_day = view.findViewById<EditText>(R.id.edit_day)
        var dayValue = edit_day.text.toString().trim { it <= ' ' }
        if (dayValue.length == 1) {
            dayValue = "0$dayValue"
        }
        val sb = StringBuilder()
        sb.append(yearValue).append(monthValue).append(dayValue)
        return sb.toString()
    }

    // Use this instance of the interface to deliver action events
    var listener: AddInfoDialogListener? = null

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the host activity implements the callback interface
        listener = try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            context as AddInfoDialogListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException(requireActivity().toString() + " must implement NoticeDialogListener")
        }
    }
}