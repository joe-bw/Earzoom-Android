package kr.co.sorizava.asrplayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.widget.Toolbar;

import org.mozilla.focus.R;

public class AsrConfigActivity extends BaseActivity implements View.OnClickListener {

    private  final  static  String TAG = "AsrConfigActivity";

    private EditText editTextModel;
    private EditText editTextAppKey, editTextAppSecret;

    RadioGroup mAuthSettingGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_asr_config);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.preference_category_asr_server));

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editTextModel = (EditText) findViewById(R.id.asr_model);
        editTextModel.setText(AppConfig.getInstance().getPrefAsrModel());

        editTextAppKey = (EditText) findViewById(R.id.asr_app_key);
        editTextAppSecret = (EditText) findViewById(R.id.asr_app_secret);

        editTextAppKey.setText(AppConfig.getInstance().getPrefAppKey());
        editTextAppSecret.setText(AppConfig.getInstance().getPrefAppSecret());

        //재시청시 재생 시점
        mAuthSettingGroup = (RadioGroup) findViewById(R.id.radio_group_asr_server_connection);
        mAuthSettingGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i) {
                    case R.id.setting_asr_auth_off:
                        AppConfig.getInstance().setPrefAsrAuthConnect(false);
                        break;
                    case R.id.setting_asr_auth_on:
                        AppConfig.getInstance().setPrefAsrAuthConnect(true);
                        break;
                }
            }
        });

        ((RadioButton)mAuthSettingGroup.getChildAt(AppConfig.getInstance().getPrefAsrAuthConnect() ? 1 : 0)).setChecked(true);

        findViewById(R.id.asr_server_connection_edit_setting_btn).setOnClickListener(this);
        findViewById(R.id.asr_server_auth_connection_edit_setting_btn).setOnClickListener(this);
    }

    public String getModel() {
        String text = editTextModel.getText().toString();
        text = text.replaceAll("[\uFEFF-\uFFFF]", "");
        return text;
    }


    public String getAppKey() {
        String text = editTextAppKey.getText().toString();
        text = text.replaceAll("[\uFEFF-\uFFFF]", "");
        return text;
    }

    public String getAppSecret() {
        String text = editTextAppSecret.getText().toString();
        text = text.replaceAll("[\uFEFF-\uFFFF]", "");
        return text;
    }

    @Override
    public void onDestroy() {
        // close any value
        AppConfig.getInstance().setPrefAsrModel(getModel());
        AppConfig.getInstance().setPrefAppKey(getAppKey());
        AppConfig.getInstance().setPrefAppSecret(getAppSecret());
        super.onDestroy();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.asr_server_connection_edit_setting_btn:
                startActivity(new Intent(this, AsrConnectionSettingActivity.class));
                break;
            case R.id.asr_server_auth_connection_edit_setting_btn:
                startActivity(new Intent(this, AsrAuthConnectionSettingActivity.class));
                break;
        }
    }
}
