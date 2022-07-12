/*
 * Create by jhong on 2022. 7. 7.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */
package com.sorizava.asrplayer.ui.intro

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.sorizava.asrplayer.config.LoginManager
import com.sorizava.asrplayer.data.vo.LoginDataVO
import com.sorizava.asrplayer.data.vo.LoginNewRequest
import com.sorizava.asrplayer.network.AppApiClient
import com.sorizava.asrplayer.network.AppApiResponse
import org.mozilla.focus.R
import org.mozilla.focus.activity.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class IntroActivity2 : AppCompatActivity() {
    private val TAG = "TEST"
    private val MY_REQUEST_CODE = 1000
    var appUpdateManager: AppUpdateManager? = null
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)


//        findViewById(R.id.imageView).setOnClickListener(v -> {
//            appStart();
//        });

//        AccessToken accessToken = AccessToken.getCurrentAccessToken();
//        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        val txtVersion = findViewById<TextView>(R.id.txtVersion)
        txtVersion.text = "Version: " + getAppVersion(this)

//        if (isLoggedIn) {
//            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
//        }

//        checkAppUpdate();
//        checkLoginInfo();
        onFirebaseConfig()
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.d("TEST", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result
                Log.d("TEST", "token: $token")
                LoginManager.instance?.putDeviceToken(token)
            })
    }

    private fun onFirebaseConfig() {
        val mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val mFirebaseRemoteConfigSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds((60 * 10).toLong())
            .build()
        mFirebaseRemoteConfig.setConfigSettingsAsync(mFirebaseRemoteConfigSettings)
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        mFirebaseRemoteConfig.fetch(3600)
            .addOnCompleteListener(this) { task: Task<Void?> ->
                if (task.isSuccessful) {
                    mFirebaseRemoteConfig.fetchAndActivate()
                    val appVersionCode = mFirebaseRemoteConfig.getString("appVersionCode")
                    compareVersion(appVersionCode)
                }
            }
    }

    /** 2021.11.01 버전 비교  */
    private fun compareVersion(appVersionCode: String) {
        val thisAppVersionCode = appVersionCode.toInt()
        Log.d("TEST", "thisAppVersionCode: $thisAppVersionCode")
        Log.d("TEST", "appVersionCode: $appVersionCode")
        if (TextUtils.isEmpty(appVersionCode)) {
            checkLoginInfo()
            return
        }
        if (thisAppVersionCode < appVersionCode.toInt()) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.txt_app_update))
                .setMessage(getString(R.string.txt_app_update_need))
                .setPositiveButton(getString(R.string.txt_app_update)) { _: DialogInterface?, _: Int ->
                    val marketLaunch = Intent(Intent.ACTION_VIEW)
                    marketLaunch.data =
                        Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                    //                        marketLaunch.setData(Uri.parse("market://details?id=" + getPackageName()));
                    startActivity(marketLaunch)
                    finish()
                }
                .setNegativeButton(getString(R.string.action_cancel)) { _: DialogInterface?, _: Int -> finish() }
            val alertDialog = builder.create()
            alertDialog.show()
        } else {
            checkLoginInfo()
        }
    }

    /** 2021.10.31 앱업데이트 확인  */
    private fun checkAppUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(this)

        // Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager!!.appUpdateInfo

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE // This example applies an immediate update. To apply a flexible update
                // instead, pass in AppUpdateType.FLEXIBLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                // Request the update.
                try {
                    appUpdateManager!!.startUpdateFlowForResult( // Pass the intent that is returned by 'getAppUpdateInfo()'.
                        appUpdateInfo,  // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                        AppUpdateType.IMMEDIATE,  // The current activity making the update request.
                        this,  // Include a request code to later monitor this update request.
                        MY_REQUEST_CODE
                    )
                } catch (e: SendIntentException) {
                    e.printStackTrace()
                }
                Log.d(TAG, "UPDATE_AVAILABLE ")
            } else {
                Log.d(TAG, "UPDATE_AVAILABLE NOT")
                checkLoginInfo()
            }
        }
        val installStateUpdatedListener: InstallStateUpdatedListener =
            object : InstallStateUpdatedListener {
                override fun onStateUpdate(state: InstallState) {
                    if (state.installStatus() == InstallStatus.DOWNLOADED) {
                        if (appUpdateManager != null) {
                            appUpdateManager!!.completeUpdate()
                        }
                    } else if (state.installStatus() == InstallStatus.INSTALLED) {
                        if (appUpdateManager != null) {
                            appUpdateManager!!.unregisterListener(this)
                        } else {
                            Log.i(
                                TAG,
                                "InstallStateUpdatedListener: state: " + state.installStatus()
                            )
                        }
                    }
                }
            }
        appUpdateManager!!.registerListener(installStateUpdatedListener)
    }

    override fun onResume() {
        super.onResume()
        if (appUpdateManager != null) {
            appUpdateManager!!.appUpdateInfo.addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    try {
                        appUpdateManager!!.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.IMMEDIATE,
                            this@IntroActivity2,
                            MY_REQUEST_CODE
                        )
                    } catch (e: SendIntentException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    /** 2021.10.31 로그인 상태 확인  */
    private fun checkLoginInfo() {
//        Handler().postDelayed({
//            //                startActivity(new Intent(IntroActivity.this, LoginActivity.class));
////                finish();
//            val birth = LoginManager.instance?.prefUserBirth
//            val phone = LoginManager.instance?.prefUserPhone
//            Log.d(TAG, "IntroActivity-birth: $birth")
//            Log.d(TAG, "IntroActivity-phone: $phone")
//            if (LoginManager.instance?.userSNSType == LoginManager.SNS_TYPE_NONE) {
////                startActivity(Intent(this@IntroActivity2, LoginActivity::class.java))
//                finish()
//            } else if (TextUtils.isEmpty(birth)) {
////                startActivity(Intent(this@IntroActivity2, LoginActivity::class.java))
//                finish()
//            } else {
//
////                    final String userID = LoginManager.getInstance().getUserId();
////                    final String userSNSType = "" + LoginManager.getInstance().getUserSNSType();
////                    final String token = "" + LoginManager.getInstance().getDeviceToken();
////
////                    LoginRequest request = new LoginRequest(userID, userSNSType, token);
//
//                if (birth != null && phone != null){
//                    val request = LoginNewRequest(birth, phone)
//                    val call = AppApiClient.apiService.requestMemberInfo(request)
//
//                    call?.enqueue(object : Callback<AppApiResponse<LoginDataVO?>?> {
//
//                        override fun onResponse(
//                            call: Call<AppApiResponse<LoginDataVO?>?>,
//                            response: Response<AppApiResponse<LoginDataVO?>?>
//                        ) {
//                            Log.d(TAG, "response.code(): " + response.code())
//                            if (response.isSuccessful) {
//                                Log.d(TAG, "isSuccessful")
//                                val result: AppApiResponse<*> = response.body()!!
//                                Log.d(TAG, "onResponse - result: $result")
//                                if (result.status == 200) {
//                                    val data = result.data as LoginDataVO
//                                    val member = data.member
//                                    Log.d(TAG, "onResponse - data.id: " + member?.id)
//                                    LoginManager.instance?.prefUserId = member?.id
//                                    appStart()
//                                } else {
//                                    reLogin()
//                                }
//                            } else {
//                                Log.d(TAG, "fail")
//                                reLogin()
//                            }
//                        }
//
//                        override fun onFailure(
//                            call: Call<AppApiResponse<LoginDataVO?>?>,
//                            t: Throwable
//                        ) {
//                            Log.d(TAG, "onFailure - result: " + t.message)
//                            reLogin()
//                        }
//                    }
//                    )
//                }
//            }
//        }, 2000)
    }



//    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
////        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == MY_REQUEST_CODE) {
//            if (resultCode != RESULT_OK) {
//                // If the update is cancelled or fails,
//                // you can request to start the update again.
//                checkAppUpdate()
//            }
//        }
//    }

    private fun reLogin() {
//        startActivity(
//            Intent(this@IntroActivity2, LoginActivity::class.java).putExtra("relogin", true)
//        )
        finish()
    }

    private fun appStart() {
        startActivity(Intent(this@IntroActivity2, MainActivity::class.java))
        finish()
    }

    private fun getAppVersion(context: Context): String {
        return try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            "1.0.0"
        }
    }

//    private val appVersionCode: Int
//        get() = try {
//            val pInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_META_DATA)
//            pInfo.versionName
//        } catch (e: PackageManager.NameNotFoundException) {
//            e.printStackTrace()
//            1
//        }
}
