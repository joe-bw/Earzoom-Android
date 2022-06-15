package kr.co.sorizava.asrplayer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.MovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.warkiz.tickseekbar.OnSeekChangeListener;
import com.warkiz.tickseekbar.SeekParams;
import com.warkiz.tickseekbar.TickSeekBar;

import kr.co.sorizava.asrplayer.helper.PlayerCaptionHelper;

import kr.co.sorizava.asrplayer.widget.FontPickerDialog;

import org.mozilla.focus.R;
import org.w3c.dom.Text;
import petrov.kristiyan.colorpicker.ColorPicker;

//자막 설정
public class SubtitleSettingActivity extends BaseActivity  {

  private  final  static  String TAG = "SubtitleSettingActivity";

  RadioGroup mSubtitleOnOffGroup;
  RadioGroup mSubtitleSyncGroup;
  RadioGroup mSubtitlePositionGroup;
  RadioGroup mSubtitleLineGroup;

  private TickSeekBar mSubtitleFontSizeSeekBar;


  TextView mSubtitleFontName;
  ImageView mSubtitleForegroundColorImage;

  private TickSeekBar mSubtitleTransparencySeekBar;

  private TextView mSzSubtitleView;

  private TypedArray mColorArray;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_subtitle_setting);

    //initActionBar(getResources().getString(R.string.back_detail), getResources().getString(R.string.setting_subtitle));

    final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    toolbar.setTitle(getResources().getString(R.string.preference_category_asr_caption));

    setSupportActionBar(toolbar);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setDisplayShowHomeEnabled(true);

    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });

    // 자막 ON/OFF
    mSubtitleOnOffGroup = (RadioGroup) findViewById(R.id.radio_group_subtitle_onoff);
    ((RadioButton)mSubtitleOnOffGroup.getChildAt(AppConfig.getInstance().getPrefSubtitleOnOff() ? 0 : 1)).setChecked(true);
    mSubtitleOnOffGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch(i) {
          case R.id.subtitle_on:
            AppConfig.getInstance().setPrefSubtitleOnOff(true);
            break;
          case R.id.subtitle_off:
//            AppConfig.getInstance().setPrefSubtitleOnOff(false);


            // 자막 보기 해제 선택시 알림창 팝업
            // jhong
            // since 210719
            new AlertDialog.Builder(SubtitleSettingActivity.this)
                    .setTitle(R.string.menu_settings)
                    .setMessage(R.string.txt_subtitle_off)
                    .setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialogInterface, int i) {
                        AppConfig.getInstance().setPrefSubtitleOnOff(false);
                      }
                    })
                    .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialogInterface, int i) {
                        ((RadioButton) findViewById(R.id.subtitle_on)).setChecked(true);
                      }
                    }).create().show();
            break;
        }
      }
    });

    //자막 동기화
    /*
    mSubtitleSyncGroup = (RadioGroup) findViewById(R.id.radio_group_subtitle_sync);
    ((RadioButton)mSubtitleSyncGroup.getChildAt(AppConfig.getInstance().getPrefSubtitleSync() ? 0 : 1)).setChecked(true);
    mSubtitleSyncGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch(i) {
          case R.id.subtitle_sync_normal:
            AppConfig.getInstance().setPrefSubtitleSync(true);
            break;
          case R.id.subtitle_sync_negative:
            AppConfig.getInstance().setPrefSubtitleSync(false);
            break;
        }
      }
    });
    */
    // 자막 위치
    mSubtitlePositionGroup = (RadioGroup) findViewById(R.id.radio_group_subtitle_position);
    ((RadioButton)mSubtitlePositionGroup.getChildAt(AppConfig.getInstance().getPrefSubtitlePosition())).setChecked(true);
    mSubtitlePositionGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch(i) {
          case R.id.subtitle_position_top:
            AppConfig.getInstance().setPrefSubtitlePosition(AppConfig.SUBTITLE_POSITION_TOP);
            break;
          case R.id.subtitle_position_bottom:
            AppConfig.getInstance().setPrefSubtitlePosition(AppConfig.SUBTITLE_POSITION_BOTTOM);
            break;
        }

        setupSubtitleView();
      }
    });


    // 자막 라인 수
    mSubtitleLineGroup = (RadioGroup) findViewById(R.id.radio_group_subtitle_line);
    ((RadioButton)mSubtitleLineGroup.getChildAt(AppConfig.getInstance().getPrefSubtitleLine() - 2)).setChecked(true);
    mSubtitleLineGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch(i) {
          case R.id.subtitle_line_2:
            AppConfig.getInstance().setPrefSubtitleLine(AppConfig.SUBTITLE_LINE_2);
            break;
          case R.id.subtitle_line_3:
            AppConfig.getInstance().setPrefSubtitleLine(AppConfig.SUBTITLE_LINE_3);
            break;
          case R.id.subtitle_line_4:
            AppConfig.getInstance().setPrefSubtitleLine(AppConfig.SUBTITLE_LINE_4);
            break;
        }

        setupSubtitleView();
      }
    });


    // 자막 폰트 크기
    mSubtitleFontSizeSeekBar = (TickSeekBar) findViewById(R.id.subtitle_font_size_seekbar);
    mSubtitleFontSizeSeekBar.setProgress(AppConfig.getInstance().getPrefSubtitleFontSize());
    mSubtitleFontSizeSeekBar.setOnSeekChangeListener(new OnSeekChangeListener() {
      @Override
      public void onSeeking(SeekParams seekParams) {
        Log.d(TAG, "mSubtitleFontSizeSeekBar position: " + seekParams.progress);// will be fired only when OK button was tapped
        AppConfig.getInstance().setPrefSubtitleFontSize(seekParams.progress);
        setupSubtitleView();
      }

      @Override
      public void onStartTrackingTouch(TickSeekBar seekBar) {
      }

      @Override
      public void onStopTrackingTouch(TickSeekBar seekBar) {
      }

    });


    // 자막 폰트
    mSubtitleFontName =  (TextView) findViewById(R.id.subtitle_font_name);
    Typeface tface = AppConfig.getInstance().convertPrefSubtitleFontTypeface(this, AppConfig.getInstance().getPrefSubtitleFont());
    mSubtitleFontName.setTypeface(tface);
    mSubtitleFontName.setText(AppConfig.getInstance().convertPrefSubtitleFontName(this, AppConfig.getInstance().getPrefSubtitleFont()));
    Button subtitleFontButton = (Button) findViewById(R.id.subtitle_font_button);

    subtitleFontButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        FontPickerDialog dialog = FontPickerDialog.newInstance(mSubtitleFont,
            new FontPickerDialog.FontPickerDialogListener() {
              @Override
              public void onFontSelected(FontPickerDialog dialog, int index) {
                Log.d(TAG, "FontPickerDialog position: " + index);// will be fired only when OK button was tapped

                Typeface tface = AppConfig.getInstance().convertPrefSubtitleFontTypeface(SubtitleSettingActivity.this, index);
                mSubtitleFontName.setTypeface(tface);
                mSubtitleFontName.setText(AppConfig.getInstance().convertPrefSubtitleFontName(SubtitleSettingActivity.this, index));

                AppConfig.getInstance().setPrefSubtitleFont(index);
                setupSubtitleView();
              }
            });
        dialog.show(getSupportFragmentManager(), "dialog");
      }
    });


    // 자막 색상
    mColorArray = getResources().obtainTypedArray(R.array.subtitle_default_colors);

    mSubtitleForegroundColorImage = findViewById(R.id.subtitle_foregroundcolor_preview);

    mSubtitleForegroundColorImage.setColorFilter(AppConfig.getInstance().getPrefSubtitleForegroundColor());

    Button subtitleForegroundColorButton = findViewById(R.id.subtitle_foregroundcolor_button);

    subtitleForegroundColorButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final ColorPicker colorPicker = new ColorPicker(SubtitleSettingActivity.this);

        int [] int_colors = new int[mColorArray.length()];
        for (int i = 0; i < mColorArray.length(); i++) {
          int_colors[i] = mColorArray.getColor(i, 0);
        }

        colorPicker
            .setTitle(getResources().getString(R.string.subtitle_foreground_color))
            .setColors(int_colors)
            .setDefaultColorButton(AppConfig.getInstance().getPrefSubtitleForegroundColor())
            .setColumns(5)
            .setRoundColorButton(false)
            .setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
              @Override
              public void onChooseColor(int position, int color) {
                 if(position != -1) {
                  mSubtitleForegroundColorImage.setColorFilter(color);
                  //mSubtitleForegroundColorImage.setBackgroundColor(color);
                  AppConfig.getInstance().setPrefSubtitleForegroundColor(color);
                  setupSubtitleView();
                }
              }

              @Override
              public void onCancel() {

              }
            }).show();
        colorPicker.getPositiveButton().setText(getResources().getText(R.string.action_ok));
        colorPicker.getNegativeButton().setText(getResources().getText(R.string.action_cancel));
      }
    });

    // 자막 배경 투명도
    mSubtitleTransparencySeekBar = (TickSeekBar) findViewById(R.id.subtitle_transparency_seekbar);
    mSubtitleTransparencySeekBar.setProgress(AppConfig.getInstance().getPrefSubtitleTransparency());
    mSubtitleTransparencySeekBar.setOnSeekChangeListener(new OnSeekChangeListener() {
      @Override
      public void onSeeking(SeekParams seekParams) {
        AppConfig.getInstance().setPrefSubtitleTransparency(seekParams.progress);
        setupSubtitleView();
      }

      @Override
      public void onStartTrackingTouch(TickSeekBar seekBar) {
      }

      @Override
      public void onStopTrackingTouch(TickSeekBar seekBar) {
      }

    });

    mSzSubtitleView = findViewById(R.id.sz_subtitle_view);

    setupSubtitleView();
  }


  @Override
  protected void onResume() {
    super.onResume();

  }

  @Override
  protected void onPause() {
    super.onPause();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
  }


  private int mSubtitlePoistion;
  private int mSubtitleFont;
  private int mSubtitleFontSize;
  private int mSubtitleLine;
  private int mSubtitleForegroundColor;
  private int mSubtitleTransparency;

  // subtitle setup
  private void setupSubtitleView() {
    mSubtitlePoistion = AppConfig.getInstance().getPrefSubtitlePosition();
    mSubtitleLine = AppConfig.getInstance().getPrefSubtitleLine();
    mSubtitleFont = AppConfig.getInstance().getPrefSubtitleFont();
    mSubtitleFontSize = AppConfig.getInstance().getPrefSubtitleFontSize();
    mSubtitleForegroundColor = AppConfig.getInstance().getPrefSubtitleForegroundColor();
    mSubtitleTransparency = AppConfig.getInstance().getPrefSubtitleTransparency();


    float textSize = UIUtils.dp2px(this, AppConfig.getInstance().convertPrefSubtitleFontSize(mSubtitleFontSize));

    // 자막 setup
    mSzSubtitleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    mSzSubtitleView.setMaxLines(mSubtitleLine);
    mSzSubtitleView.setTextColor(mSubtitleForegroundColor);  //
    mSzSubtitleView.setBackgroundColor(Color.TRANSPARENT); // 전체 투명
    mSzSubtitleView.setShadowLayer(4,0,0, Color.BLACK);
    mSzSubtitleView.setLineSpacing(0,1.0f);  // 줄간
    //mSzSubtitleView.setMovementMethod(new ScrollingMovementMethod());
    mSzSubtitleView.setMovementMethod(createMovementMethod(this));
    mSzSubtitleView.setTextIsSelectable(false);
    PlayerCaptionHelper.setSubtitleViewFont(this,
        mSzSubtitleView,
        AppConfig.getInstance().convertPrefSubtitleFontPath(this, mSubtitleFont));

    setupSubtitlePositon();
    onSubtitleTextOut("가나다라마바사\n아자차카파타하\n가나다라마바사\n아자차카파타하");
  }

  private MovementMethod createMovementMethod ( Context context ) {
    final GestureDetector detector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
      @Override
      public boolean onSingleTapUp ( MotionEvent e ) {
        return true;
      }

      @Override
      public boolean onSingleTapConfirmed ( MotionEvent e ) {
        return true;
      }
    });
    return new ScrollingMovementMethod() {
      @Override
      public boolean onTouchEvent (TextView widget, Spannable buffer, MotionEvent event ) {

        return true;
      }
    };
  }

  void setupSubtitlePositon() {
    float containerHeight = UIUtils.dp2px(this, 240);  // 240dp

    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mSzSubtitleView.getLayoutParams();

    float controlBarHeight = 10;  // gap test

    if(mSubtitlePoistion == AppConfig.SUBTITLE_POSITION_TOP) {
      layoutParams.topMargin = (int)controlBarHeight;
    }
    else {
      float textSize = UIUtils.dp2px(this, AppConfig.getInstance().convertPrefSubtitleFontSize(mSubtitleFontSize));

      float maxSubtitleHeight = (mSubtitleLine + 1) * textSize;
      layoutParams.topMargin = (int)(containerHeight - maxSubtitleHeight - controlBarHeight);
    }
    mSzSubtitleView.setLayoutParams(layoutParams);
  }

  private void onSubtitleTextOut(final String msg) {

    if(msg == null) {
      mSzSubtitleView.setText("");
    }

    Spannable spannable = new SpannableString(msg);

    BackgroundColorSpan backgroundColorSpan = new BackgroundColorSpan(
        PlayerCaptionHelper.getColorWithAlpha( Color.BLACK, AppConfig.getInstance().convertPrefSubtitleTransparency(mSubtitleTransparency)));
    spannable.setSpan(backgroundColorSpan,
        0,
        msg.length(),
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

    mSzSubtitleView.setText(spannable);
  }
}
