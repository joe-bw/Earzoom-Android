package kr.co.sorizava.asrplayerKt

import android.content.Context
import android.content.DialogInterface
import android.content.res.TypedArray
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.MovementMethod
import android.text.method.ScrollingMovementMethod
import android.text.style.BackgroundColorSpan
import android.util.Log
import android.util.TypedValue
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.google.android.material.internal.ContextUtils
import com.warkiz.tickseekbar.OnSeekChangeListener
import com.warkiz.tickseekbar.SeekParams
import com.warkiz.tickseekbar.TickSeekBar
import kr.co.sorizava.asrplayerKt.helper.PlayerCaptionHelper
import kr.co.sorizava.asrplayerKt.widget.FontPickerDialog
import org.mozilla.focus.R
import petrov.kristiyan.colorpicker.ColorPicker
import petrov.kristiyan.colorpicker.ColorPicker.OnChooseColorListener





class SubtitleSettingActivity : BaseActivity(){


    private val TAG = "SubtitleSettingActivity"

    var mSubtitleOnOffGroup: RadioGroup? = null
    var mSubtitleSyncGroup: RadioGroup? = null
    var mSubtitlePositionGroup: RadioGroup? = null
    var mSubtitleLineGroup: RadioGroup? = null

    private var mSubtitleFontSizeSeekBar: TickSeekBar? = null


    var mSubtitleFontName: TextView? = null
    //var mSubtitleForegroundColorImage: ImageView? = null
    lateinit var mSubtitleForegroundColorImage: ImageView

    private var mSubtitleTransparencySeekBar: TickSeekBar? = null

    private var mSzSubtitleView: TextView? = null

    private var mColorArray: TypedArray? = null

    private val mSubtitleSettingActivity : SubtitleSettingActivity = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subtitle_setting)

        //initActionBar(getResources().getString(R.string.back_detail), getResources().getString(R.string.setting_subtitle));
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar.title = resources.getString(R.string.preference_category_asr_caption)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }



        // 자막 ON/OFF
        mSubtitleOnOffGroup = findViewById<View>(R.id.radio_group_subtitle_onoff) as RadioGroup
        (mSubtitleOnOffGroup!!.getChildAt(if (AppConfig.getInstance()?.getPrefSubtitleOnOff()!!) 0 else 1) as RadioButton).isChecked = true
        mSubtitleOnOffGroup!!.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.subtitle_on -> AppConfig.getInstance()?.setPrefSubtitleOnOff(true)
                R.id.subtitle_off -> //            AppConfig.getInstance().setPrefSubtitleOnOff(false);

                    // 자막 보기 해제 선택시 알림창 팝업
                    // jhong
                    // since 210719
                    AlertDialog.Builder(this@SubtitleSettingActivity)
                        .setTitle(R.string.menu_settings)
                        .setMessage(R.string.txt_subtitle_off)
                        .setPositiveButton(R.string.action_ok,
                            DialogInterface.OnClickListener { dialogInterface, i ->
                                AppConfig.getInstance()?.setPrefSubtitleOnOff(false)
                            })
                        .setNegativeButton(R.string.action_cancel,
                            DialogInterface.OnClickListener { dialogInterface, i ->
                                (findViewById<View>(R.id.subtitle_on) as RadioButton).isChecked = true
                            }).create().show()
            }
        }

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
        mSubtitlePositionGroup =
            findViewById<View>(R.id.radio_group_subtitle_position) as RadioGroup
        (mSubtitlePositionGroup!!.getChildAt(AppConfig.getInstance()?.getPrefSubtitlePosition()!!) as RadioButton).isChecked =
            true
        mSubtitlePositionGroup!!.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.subtitle_position_top -> AppConfig.getInstance()?.setPrefSubtitlePosition(AppConfig.SUBTITLE_POSITION_TOP)

                R.id.subtitle_position_bottom -> AppConfig.getInstance()?.setPrefSubtitlePosition(AppConfig.SUBTITLE_POSITION_BOTTOM)
            }
            setupSubtitleView()
        }


        // 자막 라인 수
        mSubtitleLineGroup = findViewById<View>(R.id.radio_group_subtitle_line) as RadioGroup
        (mSubtitleLineGroup!!.getChildAt(AppConfig.getInstance()?.getPrefSubtitleLine()!! - 2) as RadioButton).isChecked =
            true
        mSubtitleLineGroup!!.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.subtitle_line_2 -> AppConfig.getInstance()?.setPrefSubtitleLine(AppConfig.SUBTITLE_LINE_2)

                R.id.subtitle_line_3 -> AppConfig.getInstance()?.setPrefSubtitleLine(AppConfig.SUBTITLE_LINE_3)
                R.id.subtitle_line_4 -> AppConfig.getInstance()?.setPrefSubtitleLine(AppConfig.SUBTITLE_LINE_4)
            }
            setupSubtitleView()
        }


        // 자막 폰트 크기
        mSubtitleFontSizeSeekBar =
            findViewById<View>(R.id.subtitle_font_size_seekbar) as TickSeekBar
        mSubtitleFontSizeSeekBar!!.setProgress(AppConfig.getInstance()?.getPrefSubtitleFontSize()!!.toFloat())
        mSubtitleFontSizeSeekBar!!.setOnSeekChangeListener(object : OnSeekChangeListener {
            override fun onSeeking(seekParams: SeekParams) {
                Log.d(
                    TAG,
                    "mSubtitleFontSizeSeekBar position: " + seekParams.progress
                ) // will be fired only when OK button was tapped
                AppConfig.getInstance()?.setPrefSubtitleFontSize(seekParams.progress)
                setupSubtitleView()
            }

            override fun onStartTrackingTouch(seekBar: TickSeekBar) {}
            override fun onStopTrackingTouch(seekBar: TickSeekBar) {}
        })


        // 자막 폰트
        mSubtitleFontName = findViewById<View>(R.id.subtitle_font_name) as TextView
        val tface = AppConfig.getInstance()?.convertPrefSubtitleFontTypeface(this, AppConfig.getInstance()?.getPrefSubtitleFont()!!)
        mSubtitleFontName!!.setTypeface(tface)
        mSubtitleFontName!!.text =
            AppConfig.getInstance()?.convertPrefSubtitleFontName(this, AppConfig.getInstance()?.getPrefSubtitleFont()!!)
        val subtitleFontButton = findViewById<View>(R.id.subtitle_font_button) as Button
        
        
        //////////////////코틀린 변환 실패 ( kr.co.sorizava.asrplayer.widget.FontPickerDialog.newInstance) 부분
        ///////cbw 수정필요/////////
        /*
        subtitleFontButton.setOnClickListener {
            val dialog = kr.co.sorizava.asrplayerKt.widget.FontPickerDialog.newInstance(
                mSubtitleFont
            ) { dialog, index ->
                Log.d(
                    TAG,
                    "FontPickerDialog position: $index"
                ) // will be fired only when OK button was tapped
                val tface = kr.co.sorizava.asrplayerKt.AppConfig.getInstance()
                    ?.convertPrefSubtitleFontTypeface(this@SubtitleSettingActivity, index)
                mSubtitleFontName!!.setTypeface(tface)
                mSubtitleFontName!!.text = kr.co.sorizava.asrplayerKt.AppConfig.getInstance()
                    ?.convertPrefSubtitleFontName(this@SubtitleSettingActivity, index)
                kr.co.sorizava.asrplayerKt.AppConfig.getInstance()?.setPrefSubtitleFont(index)
                setupSubtitleView()
            }
            dialog?.show(supportFragmentManager, "dialog")
        }
        */
        //val a = requireActivity().supportFragmentManager
        subtitleFontButton.setOnClickListener {
            val dialog = FontPickerDialog.newInstance(
                mSubtitleFont,
                object : FontPickerDialog.FontPickerDialogListener
                {
                    override fun onFontSelected(dialog: FontPickerDialog?, index: Int) {
//                        TODO("Not yet implemented")
                        Log.d(
                            TAG,
                            "FontPickerDialog position: $index"
                        ) // will be fired only when OK button was tapped

                        val tface = AppConfig.getInstance()
                            ?.convertPrefSubtitleFontTypeface(this@SubtitleSettingActivity, index)
                        mSubtitleFontName!!.setTypeface(tface)
                        mSubtitleFontName!!.text = AppConfig.getInstance()
                            ?.convertPrefSubtitleFontName(this@SubtitleSettingActivity, index)
                        AppConfig.getInstance()?.setPrefSubtitleFont(index)
                        setupSubtitleView()
                    }
                }
            )

            //dialog?.show()
            //dialog?.show( supportFragmentManager, "dialog")
            //dialog?.show(mSubtitleSettingActivity.supportFragmentManager, "dialog")
            dialog?.show(supportFragmentManager, "dialog")
        }


/*
        subtitleFontButton.setOnClickListener {
            val dialog = FontPickerDialog.newInstance(
                mSubtitleFont,
                object : FontPickerDialog.FontPickerDialogListener
                {
                    override fun onFontSelected(dialog: FontPickerDialog?, index: Int) {
//                        TODO("Not yet implemented")
                        Log.d(
                            TAG,
                            "FontPickerDialog position: $index"
                        ) // will be fired only when OK button was tapped
                        val tface = AppConfig.getInstance()
                            ?.convertPrefSubtitleFontTypeface(this@SubtitleSettingActivity, index)
                        mSubtitleFontName!!.setTypeface(tface)
                        mSubtitleFontName!!.text = AppConfig.getInstance()
                            ?.convertPrefSubtitleFontName(this@SubtitleSettingActivity, index)
                        AppConfig.getInstance()?.setPrefSubtitleFont(index)
                        setupSubtitleView()
                    }
                }
            )
            dialog?.show(supportFragmentManager, "dialog")
        }
*/









        // 자막 색상
        mColorArray = resources.obtainTypedArray(R.array.subtitle_default_colors)
        mSubtitleForegroundColorImage = findViewById(R.id.subtitle_foregroundcolor_preview)
        mSubtitleForegroundColorImage.setColorFilter(AppConfig.getInstance()?.getPrefSubtitleForegroundColor()!!)



        val subtitleForegroundColorButton =
            findViewById<Button>(R.id.subtitle_foregroundcolor_button)
        subtitleForegroundColorButton.setOnClickListener {
            val colorPicker = ColorPicker(this@SubtitleSettingActivity)
            val int_colors = IntArray(mColorArray!!.length())
            for (i in 0 until mColorArray!!.length()) {
                int_colors[i] = mColorArray!!.getColor(i, 0)
            }
            colorPicker
                .setTitle(resources.getString(R.string.subtitle_foreground_color))
                .setColors(*int_colors)
                .setDefaultColorButton(AppConfig.getInstance()?.getPrefSubtitleForegroundColor()!!)
                .setColumns(5)
                .setRoundColorButton(false)
                .setOnChooseColorListener(object : OnChooseColorListener {
                    override fun onChooseColor(position: Int, color: Int) {
                        if (position != -1) {
                            mSubtitleForegroundColorImage.setColorFilter(color)
                            //mSubtitleForegroundColorImage.setBackgroundColor(color);
                            AppConfig.getInstance()?.setPrefSubtitleForegroundColor(color)

                            setupSubtitleView()
                        }
                    }

                    override fun onCancel() {}
                }).show()
            colorPicker.positiveButton.text = resources.getText(R.string.action_ok)
            colorPicker.negativeButton.text = resources.getText(R.string.action_cancel)
        }

        // 자막 배경 투명도
        mSubtitleTransparencySeekBar =
            findViewById<View>(R.id.subtitle_transparency_seekbar) as TickSeekBar
        mSubtitleTransparencySeekBar!!.setProgress(AppConfig.getInstance()?.getPrefSubtitleTransparency()!!.toFloat())
        mSubtitleTransparencySeekBar!!.setOnSeekChangeListener(object : OnSeekChangeListener {
            override fun onSeeking(seekParams: SeekParams) {
                AppConfig.getInstance()?.setPrefSubtitleTransparency(seekParams.progress)
                setupSubtitleView()
            }

            override fun onStartTrackingTouch(seekBar: TickSeekBar) {}
            override fun onStopTrackingTouch(seekBar: TickSeekBar) {}
        })
        mSzSubtitleView = findViewById(R.id.sz_subtitle_view)
        setupSubtitleView()
    }


    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }


    private var mSubtitlePoistion = 0
    private var mSubtitleFont = 0
    private var mSubtitleFontSize = 0
    private var mSubtitleLine = 0
    private var mSubtitleForegroundColor = 0
    private var mSubtitleTransparency = 0

    // subtitle setup
    private fun setupSubtitleView() {
        mSubtitlePoistion = AppConfig.getInstance()?.getPrefSubtitlePosition()!!
        mSubtitleLine = AppConfig.getInstance()?.getPrefSubtitleLine()!!
        mSubtitleFont = AppConfig.getInstance()?.getPrefSubtitleFont()!!
        mSubtitleFontSize = AppConfig.getInstance()?.getPrefSubtitleFontSize()!!
        mSubtitleForegroundColor = AppConfig.getInstance()?.getPrefSubtitleForegroundColor()!!
        mSubtitleTransparency = AppConfig.getInstance()?.getPrefSubtitleTransparency()!!
        val textSize = UIUtils.dp2px(
            this,
            AppConfig.getInstance()?.convertPrefSubtitleFontSize(mSubtitleFontSize)!!.toFloat()
        ).toFloat()

        // 자막 setup
        mSzSubtitleView!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        mSzSubtitleView!!.maxLines = mSubtitleLine
        mSzSubtitleView!!.setTextColor(mSubtitleForegroundColor) //
        mSzSubtitleView!!.setBackgroundColor(Color.TRANSPARENT) // 전체 투명
        mSzSubtitleView!!.setShadowLayer(4f, 0f, 0f, Color.BLACK)
        mSzSubtitleView!!.setLineSpacing(0f, 1.0f) // 줄간
        //mSzSubtitleView.setMovementMethod(new ScrollingMovementMethod());
        mSzSubtitleView!!.movementMethod = createMovementMethod(this)
        mSzSubtitleView!!.setTextIsSelectable(false)
        PlayerCaptionHelper.setSubtitleViewFont(
            this,
            mSzSubtitleView,
            AppConfig.getInstance()?.convertPrefSubtitleFontPath(this, mSubtitleFont)
        )
        setupSubtitlePositon()
        onSubtitleTextOut("가나다라마바사\n아자차카파타하\n가나다라마바사\n아자차카파타하")
    }

    private fun createMovementMethod(context: Context): MovementMethod? {
        val detector = GestureDetector(context, object : SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                return true
            }

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                return true
            }
        })
        return object : ScrollingMovementMethod() {
            override fun onTouchEvent(
                widget: TextView,
                buffer: Spannable,
                event: MotionEvent
            ): Boolean {
                return true
            }
        }
    }

    fun setupSubtitlePositon() {
        val containerHeight = UIUtils.dp2px(this, 240f).toFloat() // 240dp
        val layoutParams = mSzSubtitleView!!.layoutParams as RelativeLayout.LayoutParams
        val controlBarHeight = 10f // gap test
        if (mSubtitlePoistion == AppConfig.SUBTITLE_POSITION_TOP) {
            layoutParams.topMargin = controlBarHeight.toInt()
        } else {
            val textSize = UIUtils.dp2px(
                this,
                AppConfig.getInstance()?.convertPrefSubtitleFontSize(mSubtitleFontSize)!!.toFloat()
            ).toFloat()
            val maxSubtitleHeight = (mSubtitleLine + 1) * textSize
            layoutParams.topMargin =
                (containerHeight - maxSubtitleHeight - controlBarHeight).toInt()
        }
        mSzSubtitleView!!.layoutParams = layoutParams
    }

    private fun onSubtitleTextOut(msg: String?) {
        if (msg == null) {
            mSzSubtitleView!!.text = ""
        }
        val spannable: Spannable = SpannableString(msg)
        val backgroundColorSpan = BackgroundColorSpan(
            PlayerCaptionHelper.getColorWithAlpha(
                Color.BLACK,
                AppConfig.getInstance()?.convertPrefSubtitleTransparency(mSubtitleTransparency)!!
            )
        )
        spannable.setSpan(
            backgroundColorSpan,
            0,
            msg!!.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        mSzSubtitleView!!.text = spannable
    }
}
