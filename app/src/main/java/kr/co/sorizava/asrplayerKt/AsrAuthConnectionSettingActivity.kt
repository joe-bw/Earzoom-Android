package kr.co.sorizava.asrplayerKt

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import org.mozilla.focus.R

class AsrAuthConnectionSettingActivity : BaseActivity() {

    val TAG = "AsrAuthConnectionSettingActivity"

    var editTextAuthTokenUrl: EditText? = null
    private  var editTextAsrServerUrl:EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_asr_auth_connection_setting)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar.setTitle(getResources().getString(R.string.setting_asr_server_auth_connection))
        setSupportActionBar(toolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()?.setDisplayShowHomeEnabled(true)

        toolbar.setNavigationOnClickListener { finish() }

        editTextAuthTokenUrl = findViewById<View>(R.id.asr_auth_url) as EditText?
        editTextAsrServerUrl = findViewById<View>(R.id.asr_ws_url) as EditText
        editTextAuthTokenUrl!!.setText(AppConfig.getInstance()?.getPrefAuthTokenUrl())
        editTextAsrServerUrl!!.setText(AppConfig.getInstance()?.getPrefAuthTokenUrl())
    }

    fun getAuthTokenUrl(): String? {
        var text = editTextAuthTokenUrl!!.text.toString()
        text = text.replace("[\uFEFF-\uFFFF]".toRegex(), "")
        return text
    }

    fun getAsrServerUrl(): String? {
        var text: String = editTextAsrServerUrl!!.getText().toString()
        text = text.replace("[\uFEFF-\uFFFF]".toRegex(), "")
        return text
    }

    override fun onDestroy() {
        // close any value
        AppConfig.getInstance()?.setPrefAuthTokenUrl( getAuthTokenUrl())
        AppConfig.getInstance()?.setPrefAsrAuthUrl( getAsrServerUrl())
        super.onDestroy()
    }
}