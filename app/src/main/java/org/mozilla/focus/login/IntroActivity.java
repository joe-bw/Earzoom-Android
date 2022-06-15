package org.mozilla.focus.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.mozilla.focus.R;
import org.mozilla.focus.activity.MainActivity;
import org.mozilla.focus.login.data.AppApiClient;
import org.mozilla.focus.login.data.AppApiResponse;
import org.mozilla.focus.login.data.LoginDataVO;
import org.mozilla.focus.login.data.LoginManager;
import org.mozilla.focus.login.data.LoginMemberVO;
import org.mozilla.focus.login.data.LoginNewRequest;
import org.mozilla.focus.login.ui.login.LoginActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IntroActivity extends AppCompatActivity {

    private final String TAG = "TEST";

    private final int MY_REQUEST_CODE = 1000;

    AppUpdateManager appUpdateManager = null;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);


//        findViewById(R.id.imageView).setOnClickListener(v -> {
//            appStart();
//        });

//        AccessToken accessToken = AccessToken.getCurrentAccessToken();
//        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        TextView txtVersion = findViewById(R.id.txtVersion);
        txtVersion.setText("Version: " + getAppVersion(this));

//        if (isLoggedIn) {
//            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
//        }

//        checkAppUpdate();
//        checkLoginInfo();

        onFirebaseConfig();

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.d("TEST", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        Log.d("TEST", "token: " + token);
                        LoginManager.getInstance().putDeviceToken(token);
                    }
                });
    }

    private void onFirebaseConfig() {

        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings mFirebaseRemoteConfigSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(60 * 10)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(mFirebaseRemoteConfigSettings);
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
        mFirebaseRemoteConfig.fetch(3600)
                .addOnCompleteListener(this, task -> {
                    if(task.isSuccessful()){
                        mFirebaseRemoteConfig.fetchAndActivate();
                        String appVersionCode  = mFirebaseRemoteConfig.getString("appVersionCode");
                        compareVersion(appVersionCode);
                    }});
    }

    /** 2021.11.01 버전 비교 */
    private void compareVersion(String appVersionCode) {

        int thisAppVersionCode = getAppVersionCode();

        Log.d("TEST", "thisAppVersionCode: " + thisAppVersionCode);
        Log.d("TEST", "appVersionCode: " + appVersionCode);

        if (TextUtils.isEmpty(appVersionCode)) {
            checkLoginInfo();
            return;
        }

        if (thisAppVersionCode < Integer.parseInt(appVersionCode)) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.txt_app_update))
                    .setMessage(getString(R.string.txt_app_update_need))
                    .setPositiveButton(getString(R.string.txt_app_update), (dialog, which) -> {
                        Intent marketLaunch = new Intent(Intent.ACTION_VIEW);
                        marketLaunch.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
//                        marketLaunch.setData(Uri.parse("market://details?id=" + getPackageName()));
                        startActivity(marketLaunch);
                        finish();
                    })
                    .setNegativeButton(getString(R.string.action_cancel), (dialog, which) -> {
                        finish();
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else {
            checkLoginInfo();
        }
    }

    /** 2021.10.31 앱업데이트 확인 */
    private void checkAppUpdate() {

        appUpdateManager = AppUpdateManagerFactory.create(this);

        // Returns an intent object that you use to check for an update.
        com.google.android.play.core.tasks.Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // This example applies an immediate update. To apply a flexible update
                    // instead, pass in AppUpdateType.FLEXIBLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                // Request the update.

                try {
                    appUpdateManager.startUpdateFlowForResult(
                            // Pass the intent that is returned by 'getAppUpdateInfo()'.
                            appUpdateInfo,
                            // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                            AppUpdateType.IMMEDIATE,
                            // The current activity making the update request.
                            this,
                            // Include a request code to later monitor this update request.
                            MY_REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "UPDATE_AVAILABLE ");
            } else {
                Log.d(TAG, "UPDATE_AVAILABLE NOT");
                checkLoginInfo();
            }
        });

        InstallStateUpdatedListener installStateUpdatedListener = new InstallStateUpdatedListener() {
            @Override
            public void onStateUpdate(InstallState state) {
                if (state.installStatus() == InstallStatus.DOWNLOADED) {
                    if (appUpdateManager != null) {
                        appUpdateManager.completeUpdate();
                    }
                } else if (state.installStatus() == InstallStatus.INSTALLED) {
                    if (appUpdateManager != null) {
                        appUpdateManager.unregisterListener(this);
                    } else {
                        Log.i(TAG, "InstallStateUpdatedListener: state: " + state.installStatus());
                    }
                }
            }
        };
        appUpdateManager.registerListener(installStateUpdatedListener);
    }

    @Override protected void onResume() {
        super.onResume();

        if (appUpdateManager != null) {
            appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    try {
                        appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, IntroActivity.this, MY_REQUEST_CODE);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /** 2021.10.31 로그인 상태 확인 */
    private void checkLoginInfo() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

//                startActivity(new Intent(IntroActivity.this, LoginActivity.class));
//                finish();

                String birth = LoginManager.getInstance().getPrefUserBirth();
                String phone = LoginManager.getInstance().getPrefUserPhone();

                Log.d(TAG, "IntroActivity-birth: " + birth);
                Log.d(TAG, "IntroActivity-phone: " + phone);

                if (LoginManager.getInstance().getUserSNSType() == LoginManager.SNS_TYPE_NONE) {
                    startActivity(new Intent(IntroActivity.this, LoginActivity.class));
                    finish();
                } else if (TextUtils.isEmpty(birth)) {
                    startActivity(new Intent(IntroActivity.this, LoginActivity.class));
                    finish();
                } else {

//                    final String userID = LoginManager.getInstance().getUserId();
//                    final String userSNSType = "" + LoginManager.getInstance().getUserSNSType();
//                    final String token = "" + LoginManager.getInstance().getDeviceToken();
//
//                    LoginRequest request = new LoginRequest(userID, userSNSType, token);

                    LoginNewRequest request = new LoginNewRequest(birth, phone);

                    Call<AppApiResponse<LoginDataVO>> call = AppApiClient.getApiService().requestMemberInfo(request);
                    call.enqueue(new Callback<AppApiResponse<LoginDataVO>>() {
                        @Override
                        public void onResponse(Call<AppApiResponse<LoginDataVO>> call, Response<AppApiResponse<LoginDataVO>> response) {

                            Log.d(TAG, "response.code(): " + response.code());

                            if (response.isSuccessful()) {
                                Log.d(TAG, "isSuccessful");
                                AppApiResponse result = response.body();
                                Log.d(TAG, "onResponse - result: " + result.toString());
                                if (result.status == 200) {

                                    LoginDataVO data = (LoginDataVO) result.data;
                                    LoginMemberVO member = data.member;
                                    Log.d(TAG, "onResponse - data.id: " + member.id);
                                    LoginManager.getInstance().setPrefUserId(member.id);

                                    appStart();
                                } else {
                                    reLogin();
                                }
                            } else {
                                Log.d(TAG, "fail");
                                reLogin();
                            }
                        }

                        @Override
                        public void onFailure(Call<AppApiResponse<LoginDataVO>> call, Throwable t) {
                            Log.d(TAG, "onFailure - result: " + t.getMessage());
                            reLogin();
                        }
                    });
                }
            }
        }, 2000);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                // If the update is cancelled or fails,
                // you can request to start the update again.
                checkAppUpdate();
            }
        }
    }

    private void reLogin() {
        startActivity(new Intent(IntroActivity.this, LoginActivity.class)
        .putExtra("relogin", true));
        finish();
    }
    private void appStart() {
        startActivity(new Intent(IntroActivity.this, MainActivity.class));
        finish();
    }

    private String getAppVersion(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "1.0.0";
        }
    }

    private int getAppVersionCode() {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_META_DATA);

            return pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 1;
        }
    }
}