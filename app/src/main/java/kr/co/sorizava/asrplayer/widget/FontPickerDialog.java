package kr.co.sorizava.asrplayer.widget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import org.mozilla.focus.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import kr.co.sorizava.asrplayer.AppConfig;
//import kr.co.sorizava.asrplayer.R;
import kr.co.sorizava.asrplayer.SubtitleSettingActivity;
import petrov.kristiyan.colorpicker.ColorPal;

/** This class gives a dialog for selecting a font */
public class FontPickerDialog extends DialogFragment {

  private List<String> mFontPaths; // list of file paths for the available fonts
  private List<String> mFontNames; // font names of the available fonts
  private int mSelectedPosition;
  private String mSelectedFont;
  private FontPickerDialogListener mListener;

  public static FontPickerDialog newInstance(int selectedPosition, FontPickerDialogListener fontPickerDialogListener) {
    FontPickerDialog fontPickerDialog = new FontPickerDialog();
    fontPickerDialog.mSelectedPosition = selectedPosition;
    fontPickerDialog.mListener = fontPickerDialogListener;
    return fontPickerDialog;
  }

  // create callback method to pass back the selected font
  public interface FontPickerDialogListener {
    /**
     * This method is called when a font is selected in the FontPickerDialog
     *
     * @param dialog The dialog used to pick the font. Use dialog.getSelectedFont() to access the
     *     pathname of the chosen font
     */
    void onFontSelected(FontPickerDialog dialog, int index);
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {

    Context mContext = getActivity();

    mFontPaths = new ArrayList<>();
    mFontNames = new ArrayList<>();

    mFontPaths.add("");

    String[] fontPaths = mContext.getResources().getStringArray(R.array.subtitle_font_list);
    for (int i = 0; i < fontPaths.length; i++) {
      mFontPaths.add(fontPaths[i]);
    }

    mFontNames.add(mContext.getResources().getString(R.string.subtitle_font_0)); // system default
    mFontNames.add(mContext.getResources().getString(R.string.subtitle_font_1)); // system default
    mFontNames.add(mContext.getResources().getString(R.string.subtitle_font_2)); // system default
    mFontNames.add(mContext.getResources().getString(R.string.subtitle_font_3)); // system default
    mFontNames.add(mContext.getResources().getString(R.string.subtitle_font_4)); // system default


    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    LayoutInflater inflater = getActivity().getLayoutInflater();

    View view = inflater.inflate(R.layout.dialog_font_picker, null);
    FontAdapter adapter = new FontAdapter(mContext);

    builder.setSingleChoiceItems(adapter, mSelectedPosition, new OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {
        mSelectedPosition = i;
        mSelectedFont = mFontPaths.get(i);
        adapter.notifyDataSetChanged();
      }
    });

    LinearLayout titleLinearLayout = new LinearLayout(mContext);

    titleLinearLayout.setLayoutParams(
        new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    titleLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

    TextView title = new TextView(mContext);
    title.setText(R.string.subtitle_font);
    title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
    title.setTypeface(null, Typeface.BOLD);
    title.setTextColor(ContextCompat.getColor(mContext, R.color.black));
    title.setPadding(40, 40, 40, 40);
    titleLinearLayout.setGravity(Gravity.CENTER);

    titleLinearLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorWhite));
    titleLinearLayout.addView(title);

    builder.setCustomTitle(titleLinearLayout);
    builder.setNegativeButton(
        getString(R.string.action_cancel).toUpperCase(),
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            // don't have to do anything on cancel
          }
        });


    builder.setPositiveButton(
        getString(R.string.ok).toUpperCase(),
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            // don't have to do anything on cancel
            mListener.onFontSelected(FontPickerDialog.this, mSelectedPosition);
          }
        });

    Dialog pickDialog = builder.create();
    pickDialog.getWindow().setBackgroundDrawableResource(R.color.white);
    return pickDialog;
  }

  /**
   * Callback method that is called once a font has been selected and the fontpickerdialog closes.
   *
   * @return The pathname of the font that was selected
   */
  public String getSelectedFont() {
    return mSelectedFont;
  }

  private class FontAdapter extends BaseAdapter {
    private Context mContext;

    public FontAdapter(Context c) {
      mContext = c;
    }

    @Override
    public int getCount() {
      return mFontNames.size();
    }

    @Override
    public Object getItem(int position) {
      return mFontNames.get(position);
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      TextView view = (TextView) convertView;

      if (view == null) {
        view = new TextView(mContext);
      }

      view.setPadding(40, 20, 40, 20);

      Typeface tface = AppConfig
          .getInstance().convertPrefSubtitleFontTypeface(mContext, position);
      view.setTypeface(tface);
      view.setText(mFontNames.get(position));
      view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
      view.setTextColor(getResources().getColor(R.color.black));
      if(position == mSelectedPosition) {
        view.setBackgroundColor(getResources().getColor(R.color.golden_pressed));
      }
      else {
        view.setBackgroundColor(getResources().getColor(R.color.white));
      }

      return view;
    }
  }
}
