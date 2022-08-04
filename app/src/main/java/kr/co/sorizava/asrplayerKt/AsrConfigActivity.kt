package kr.co.sorizava.asrplayerKt

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.widget.Toolbar

import org.mozilla.focus.R

class AsrConfigActivity : BaseActivity() , View.OnClickListener {

    val TAG = "AsrConfigActivity"

    var editTextModel: EditText? = null
    var editTextAppKey: EditText? = null
    private  var editTextAppSecret:EditText? = null


    var mAuthSettingGroup: RadioGroup? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_asr_config)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar.setTitle(getResources().getString(R.string.preference_category_asr_server))
        setSupportActionBar(toolbar)
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()!!.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }
        editTextModel = findViewById<View>(R.id.asr_model) as EditText?
        editTextModel!!.setText(AppConfig.getInstance()?.getPrefAsrModel())
        editTextAppKey = findViewById<View>(R.id.asr_app_key) as EditText?
        editTextAppSecret = findViewById<View>(R.id.asr_app_secret) as EditText
        editTextAppKey!!.setText(AppConfig.getInstance()?.getPrefAppKey())
        editTextAppSecret!!.setText(AppConfig.getInstance()?.getPrefAppSecret())

        //재시청시 재생 시점
        mAuthSettingGroup =
            findViewById<View>(R.id.radio_group_asr_server_connection) as RadioGroup?
        mAuthSettingGroup!!.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.setting_asr_auth_off -> AppConfig.getInstance()?.setPrefAsrAuthConnect(false)
                R.id.setting_asr_auth_on -> AppConfig.getInstance()?.setPrefAsrAuthConnect(true)
            }
        }
        (mAuthSettingGroup!!.getChildAt(if (AppConfig.getInstance()?.getPrefAsrAuthConnect()!!) 1 else 0) as RadioButton).isChecked =
            true
        findViewById<View>(R.id.asr_server_connection_edit_setting_btn).setOnClickListener(this)
        findViewById<View>(R.id.asr_server_auth_connection_edit_setting_btn).setOnClickListener(this)
    }

    fun getModel(): String? {
        var text = editTextModel!!.text.toString()
        text = text.replace("[\uFEFF-\uFFFF]".toRegex(), "")
        return text
    }


    fun getAppKey(): String? {
        var text = editTextAppKey!!.text.toString()
        text = text.replace("[\uFEFF-\uFFFF]".toRegex(), "")
        return text
    }

    fun getAppSecret(): String? {
        var text: String = editTextAppSecret!!.getText().toString()
        text = text.replace("[\uFEFF-\uFFFF]".toRegex(), "")
        return text
    }

    override fun onDestroy() {
        // close any value
        AppConfig.getInstance()?.setPrefAsrModel(getModel())
        AppConfig.getInstance()?.setPrefAppKey(getAppKey())
        AppConfig.getInstance()?.setPrefAppSecret(getAppSecret())
        super.onDestroy()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.asr_server_connection_edit_setting_btn -> startActivity(
                Intent(
                    this,
                    AsrConnectionSettingActivity::class.java
                )
            )
            R.id.asr_server_auth_connection_edit_setting_btn -> startActivity(
                Intent(
                    this,
                    AsrAuthConnectionSettingActivity::class.java
                )
            )
        }
    }
}
