package kr.co.sorizava.asrplayerKt

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.widget.Toolbar
import org.mozilla.focus.R

class AsrConnectionSettingActivity : BaseActivity() {

    val TAG = "AsrConnectionSettingActivity"

    var editTextAsrServerUrl: EditText? = null

    var mProjectionOnOffGroup: RadioGroup? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_asr_connection_setting)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar.setTitle(getResources().getString(R.string.setting_asr_server_connection))
        setSupportActionBar(toolbar)
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()!!.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        // 일반 연결시 project param 사용 여부
        mProjectionOnOffGroup =
            findViewById<View>(R.id.radio_group_asr_param_project_onoff) as RadioGroup?
        mProjectionOnOffGroup!!.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.setting_asr_param_project_off -> AppConfig.getInstance()?.setPrefAsrUseParamProject(false)

                R.id.setting_asr_param_project_on -> AppConfig.getInstance()?.setPrefAsrUseParamProject(true)

            }
        }
        (mProjectionOnOffGroup!!.getChildAt(if (AppConfig.getInstance()?.getPrefAsrUseParamProject()!!) 1 else 0) as RadioButton).isChecked =
            true
        editTextAsrServerUrl = findViewById<View>(R.id.asr_ws_url) as EditText?
        editTextAsrServerUrl!!.setText(AppConfig.getInstance()?.getPrefAsrUrl())
    }


    fun getAsrServerUrl(): String? {
        var text = editTextAsrServerUrl!!.text.toString()
        text = text.replace("[\uFEFF-\uFFFF]".toRegex(), "")
        return text
    }

    override fun onDestroy() {
        // close any value
        AppConfig.getInstance()?.setPrefAsrUrl(getAsrServerUrl())
        super.onDestroy()
    }
}