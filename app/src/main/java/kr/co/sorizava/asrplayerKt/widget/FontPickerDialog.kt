package kr.co.sorizava.asrplayerKt.widget

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import kr.co.sorizava.asrplayerKt.AppConfig
import org.mozilla.focus.R
import java.util.*

class FontPickerDialog : DialogFragment() {

    //private var mFontPaths : MutableList<String>? = null // list of file paths for the available fonts
    //private var mFontNames : MutableList<String>? = null // font names of the available fonts

    private var mFontPaths : ArrayList<String> = ArrayList<String>() // list of file paths for the available fonts
    public var mFontNames : ArrayList<String> = ArrayList<String>() // font names of the available fonts

    public var mSelectedPosition = 0
    private var mSelectedFont: String? = null
    public var mListener: FontPickerDialogListener? = null


    /*
      public static FontPickerDialog newInstance(int selectedPosition, FontPickerDialogListener fontPickerDialogListener) {
        FontPickerDialog fontPickerDialog = new FontPickerDialog();
        fontPickerDialog.mSelectedPosition = selectedPosition;
        fontPickerDialog.mListener = fontPickerDialogListener;
        return fontPickerDialog;
      }
     */



    companion object
    {

        fun newInstance(
            selectedPosition: Int,
            fontPickerDialogListener: FontPickerDialogListener,
        ): FontPickerDialog? {
            val fontPickerDialog = FontPickerDialog()
            fontPickerDialog.mSelectedPosition = selectedPosition
            fontPickerDialog.mListener = fontPickerDialogListener
            return fontPickerDialog
        }


    }

    // create callback method to pass back the selected font
    interface FontPickerDialogListener {
        /**
         * This method is called when a font is selected in the FontPickerDialog
         *
         * @param dialog The dialog used to pick the font. Use dialog.getSelectedFont() to access the
         * pathname of the chosen font
         */
        fun onFontSelected(dialog: FontPickerDialog?, index: Int)
    }


    var adapter : FontAdapter? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val mContext: Context? = activity
        mFontPaths = ArrayList<String>()
        mFontNames = ArrayList<String>()
        mFontPaths.add("")

        //(mFontPaths as ArrayList<String>).add("")
        val fontPaths = mContext!!.resources.getStringArray(R.array.subtitle_font_list)
        for (i in fontPaths.indices) {
            mFontPaths.add(fontPaths[i])
        }
        mFontNames.add(mContext.resources.getString(R.string.subtitle_font_0)) // system default
        mFontNames.add(mContext.resources.getString(R.string.subtitle_font_1)) // system default
        mFontNames.add(mContext.resources.getString(R.string.subtitle_font_2)) // system default
        mFontNames.add(mContext.resources.getString(R.string.subtitle_font_3)) // system default
        mFontNames.add(mContext.resources.getString(R.string.subtitle_font_4)) // system default
        /*
        val builder = AlertDialog.Builder(activity)
        val inflater = activity!!.layoutInflater
        */



        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater

        val view: View = inflater.inflate(R.layout.dialog_font_picker, null)


        adapter = FontAdapter(mContext,mFontNames)
        //adapter = FontAdapter(mContext)
        //adapter = FontAdapter(requireActivity())

        builder.setSingleChoiceItems(
            adapter, mSelectedPosition
        ) { dialogInterface, i ->
            adapter!!.mSelectedPosition = i
            //mSelectedPosition = i
            mSelectedFont = mFontPaths.get(i)
            adapter!!.notifyDataSetChanged()
        }
        
        
        adapter!!.notifyDataSetChanged()

        val titleLinearLayout = LinearLayout(mContext)
        titleLinearLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        titleLinearLayout.orientation = LinearLayout.HORIZONTAL
        val title = TextView(mContext)
        title.setText(R.string.subtitle_font)
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        title.setTypeface(null, Typeface.BOLD)
        title.setTextColor(ContextCompat.getColor(mContext, R.color.black))
        title.setPadding(40, 40, 40, 40)
        titleLinearLayout.gravity = Gravity.CENTER
        titleLinearLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorWhite))
        titleLinearLayout.addView(title)

        builder.setCustomTitle(titleLinearLayout)
        builder.setNegativeButton(
            getString(R.string.action_cancel).uppercase(Locale.getDefault())
        ) { dialog, id ->
            // don't have to do anything on cancel
        }
        builder.setPositiveButton(
            getString(R.string.ok).uppercase(Locale.getDefault())
        ) { dialog, id -> // don't have to do anything on cancel
            mListener!!.onFontSelected(this@FontPickerDialog, mSelectedPosition)
        }


        val pickDialog: Dialog = builder.create()
        pickDialog.window!!.setBackgroundDrawableResource(R.color.white)
        return pickDialog
    }

    /**
     * Callback method that is called once a font has been selected and the fontpickerdialog closes.
     *
     * @return The pathname of the font that was selected
     */
    fun getSelectedFont(): String? {
        return mSelectedFont
    }



    class FontAdapter(
        val mContext: Context,
        val mFontNames: List<String>,
    ) : BaseAdapter() {


        override fun getCount(): Int {
            return mFontNames.size
        }

        override fun getItem(position: Int): Any {
            return mFontNames.get(position)
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            //TODO("Not yet implemented")

            Log.d("getView","선택된 인덱스:" + position.toString())
            var view : TextView? = null

            if( convertView == null )
            {
                view = TextView(mContext)
            }
            else
            {
                view = convertView as TextView
            }
            if (view == null) {
                view = TextView(mContext)
            }
            
            view.setPadding(40, 20, 40, 20)
            val tface = AppConfig.getInstance()?.convertPrefSubtitleFontTypeface(mContext, position)
            view.setTypeface(tface)
            view.setText(mFontNames.get(position))
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            view.setTextColor(mContext.resources.getColor(R.color.black))


            if (position == this.mSelectedPosition) {
                view.setBackgroundColor(mContext.resources.getColor(R.color.golden_pressed))
            } else {
                view.setBackgroundColor(mContext.resources.getColor(R.color.white))
            }

            return view
        }
        var mSelectedPosition :Int = 0

        /*
        override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
            val a = 1

            var view = convertView as TextView
            if (view == null) {
                view = TextView(mContext)
            }
            view.setPadding(40, 20, 40, 20)
            val tface = AppConfig.getInstance()?.convertPrefSubtitleFontTypeface(mContext, position)
            view.setTypeface(tface)
            view.setText(mFontNames.get(position))
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            view.setTextColor(getResources().getColor(R.color.black))
            if (position == mSelectedPosition) {
                view.setBackgroundColor(getResources().getColor(R.color.golden_pressed))
            } else {
                view.setBackgroundColor(getResources().getColor(R.color.white))
            }
            return view
        }
        */
    }
}
