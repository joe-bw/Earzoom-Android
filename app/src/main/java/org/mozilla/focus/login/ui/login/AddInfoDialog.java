package org.mozilla.focus.login.ui.login;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.snackbar.Snackbar;

import org.mozilla.focus.R;

import java.util.Calendar;
import java.util.Objects;

public class AddInfoDialog extends DialogFragment {

    private View view = null;

    public interface AddInfoDialogListener {
        void onDialogPositiveClick(DialogFragment dialog, String date, String birth);
        void onDialogNegativeClick(DialogFragment dialog);
        void onDialogPrivacyClick(DialogFragment dialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_add_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        getDialog().getWindow().setLayout(displayMetrics.widthPixels, displayMetrics.heightPixels);

//        ((EditText)view.findViewById(R.id.edit_phone)).setText("010");

        CheckBox checkBox = (CheckBox)view.findViewById(R.id.chk_privacy);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkBox.setTextColor(Color.WHITE);
                    checkBox.setError("", null);
                }
            }
        });

        view.findViewById(R.id.btn_privacy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDialogPrivacyClick(AddInfoDialog.this);
            }
        });

        view.findViewById(R.id.btn_birth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();

                DatePickerDialog pickerDialog = new DatePickerDialog(
                        getActivity(), android.R.style.Theme_Holo_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker picker, int year, int month, int dayOfMonth) {

                        ((EditText)view.findViewById(R.id.edit_year)).setText("" + year);
                        ((EditText)view.findViewById(R.id.edit_month)).setText("" + (month + 1));
                        ((EditText)view.findViewById(R.id.edit_day)).setText("" + dayOfMonth);

                        v.setVisibility(View.GONE);
                        view.findViewById(R.id.layout_birth).setVisibility(View.VISIBLE);

                    }
                }, 2000, 0, 1
                );

                pickerDialog.show();

            }
        });

        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDialogNegativeClick(AddInfoDialog.this);
            }
        });

        view.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!checkBox.isChecked()) {

                    checkBox.setError("동의 필요");
                    checkBox.setTextColor(Color.RED);
                    checkBox.setFocusable(true);

                    return;
                }

                if (isValid(view)) {
                    listener.onDialogPositiveClick(AddInfoDialog.this, getDateTime(view), getPhoneValue(view));
                } else {
                    Snackbar.make(view, getString(R.string.txt_auth_need), Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        dialog.getWindow().setAttributes(lpWindow);

        InsetDrawable insetDrawable = new InsetDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT), 0);
        dialog.getWindow().setBackgroundDrawable(insetDrawable);

        return dialog;
    }

    private boolean isValid(View view) {

        EditText edit_year = view.findViewById(R.id.edit_year);
        String yearValue = edit_year.getText().toString().trim();
        if (yearValue.isEmpty()) return false;

        EditText edit_month = view.findViewById(R.id.edit_month);
        String monthValue = edit_month.getText().toString().trim();
        if (monthValue.isEmpty()) return false;

        EditText edit_day = view.findViewById(R.id.edit_day);
        String dayValue = edit_day.getText().toString().trim();
        if (dayValue.isEmpty()) return false;

        EditText edit_phone = view.findViewById(R.id.edit_phone);
        String phoneValue = edit_phone.getText().toString().trim();
        if (phoneValue.isEmpty()) return false;

        return true;
    }

    public String getPhoneValue(View view) {

        EditText edit_phone = view.findViewById(R.id.edit_phone);
        String phoneValue = edit_phone.getText().toString().trim();

        return phoneValue;
    }

    public String getDateTime(View view) {

        EditText edit_year = view.findViewById(R.id.edit_year);
        String yearValue = edit_year.getText().toString().trim();

        EditText edit_month = view.findViewById(R.id.edit_month);
        String monthValue = edit_month.getText().toString().trim();
        if (monthValue.length() == 1) {
            monthValue = "0" + monthValue;
        }

        EditText edit_day = view.findViewById(R.id.edit_day);
        String dayValue = edit_day.getText().toString().trim();
        if (dayValue.length() == 1) {
            dayValue = "0" + dayValue;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(yearValue).append(monthValue).append(dayValue);

        return sb.toString();
    }

    // Use this instance of the interface to deliver action events
    AddInfoDialogListener listener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (AddInfoDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(requireActivity().toString() + " must implement NoticeDialogListener");
        }
    }

}
