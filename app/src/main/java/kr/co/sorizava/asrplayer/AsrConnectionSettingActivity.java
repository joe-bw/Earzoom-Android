package kr.co.sorizava.asrplayer;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.widget.Toolbar;

import org.mozilla.focus.R;

public class AsrConnectionSettingActivity extends BaseActivity {

    private final static String TAG = "AsrConnectionSettingActivity";

    private EditText editTextAsrServerUrl;

    RadioGroup mProjectionOnOffGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_asr_connection_setting);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.setting_asr_server_connection));

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 일반 연결시 project param 사용 여부
        mProjectionOnOffGroup = (RadioGroup) findViewById(R.id.radio_group_asr_param_project_onoff);
        mProjectionOnOffGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i) {
                    case R.id.setting_asr_param_project_off:
                        AppConfig.getInstance().setPrefAsrUseParamProject(false);
                        break;
                    case R.id.setting_asr_param_project_on:
                        AppConfig.getInstance().setPrefAsrUseParamProject(true);
                        break;
                }
            }
        });

        ((RadioButton)mProjectionOnOffGroup.getChildAt(AppConfig.getInstance().getPrefAsrUseParamProject() ? 1 : 0)).setChecked(true);

        editTextAsrServerUrl = (EditText) findViewById(R.id.asr_ws_url);
        editTextAsrServerUrl.setText(AppConfig.getInstance().getPrefAsrUrl());
    }


    public String getAsrServerUrl() {
        String text = editTextAsrServerUrl.getText().toString();
        text = text.replaceAll("[\uFEFF-\uFFFF]", "");
        return text;
    }

    @Override
    public void onDestroy() {
        // close any value
        AppConfig.getInstance().setPrefAsrUrl(getAsrServerUrl());
        super.onDestroy();
    }
}