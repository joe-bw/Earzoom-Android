package kr.co.sorizava.asrplayer;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;

import org.mozilla.focus.R;

public class AsrAuthConnectionSettingActivity extends BaseActivity {

    private final static String TAG = "AsrAuthConnectionSettingActivity";

    private EditText editTextAuthTokenUrl, editTextAsrServerUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_asr_auth_connection_setting);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.setting_asr_server_auth_connection));

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editTextAuthTokenUrl = (EditText) findViewById(R.id.asr_auth_url);
        editTextAsrServerUrl = (EditText) findViewById(R.id.asr_ws_url);

        editTextAuthTokenUrl.setText(AppConfig.getInstance().getPrefAuthTokenUrl());
        editTextAsrServerUrl.setText(AppConfig.getInstance().getPrefAsrAuthUrl());
    }

    public String getAuthTokenUrl() {
        String text = editTextAuthTokenUrl.getText().toString();
        text = text.replaceAll("[\uFEFF-\uFFFF]", "");
        return text;
    }

    public String getAsrServerUrl() {
        String text = editTextAsrServerUrl.getText().toString();
        text = text.replaceAll("[\uFEFF-\uFFFF]", "");
        return text;
    }

    @Override
    public void onDestroy() {
        // close any value
        AppConfig.getInstance().setPrefAuthTokenUrl(getAuthTokenUrl());
        AppConfig.getInstance().setPrefAsrAuthUrl(getAsrServerUrl());
        super.onDestroy();
    }
}