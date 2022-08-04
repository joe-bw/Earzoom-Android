/*
 * Create by jhong on 2022. 7. 18.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.ui.login

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.navercorp.nid.NaverIdLoginSDK
import com.sorizava.asrplayer.config.LOGIN_TYPE_RELOGIN
import com.sorizava.asrplayer.config.NAVER_CLIENT_ID
import com.sorizava.asrplayer.config.SorizavaLoginManager
import com.sorizava.asrplayer.extension.config
import com.sorizava.asrplayer.ui.base.BaseActivity
import org.mozilla.focus.R
import org.mozilla.focus.databinding.ActivityLogin2Binding

class LoginActivity : BaseActivity<ActivityLogin2Binding>(ActivityLogin2Binding::inflate) {

    private lateinit var viewModel: LoginViewModel

    override fun initView(savedInstanceState: Bundle?) {

        viewModel = ViewModelProvider(this, LoginViewModelFactory(this.application))
            .get(LoginViewModel::class.java)

        viewModel.initLauncher(this)

        val bundle: Bundle? = null

        if (intent.extras != null) {
            val isReLogin = intent.extras!!.getBoolean(LOGIN_TYPE_RELOGIN, false)
            if (isReLogin) {
                bundle?.putBoolean(LOGIN_TYPE_RELOGIN, isReLogin)
            }
        }

        NaverIdLoginSDK.apply {
            showDevelopersLog(true)
            initialize(application, config.naverClientId, config.naverClientSecret, config.naverClientName)
            isShowMarketLink = true
            isShowBottomTab = true
        }

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
                SorizavaLoginManager.instance?.putDeviceToken(token)
            })

        val fragment = LoginFragment.newInstance()
        bundle?.apply {
            fragment.arguments = bundle
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commitNow()
        }
    }

    private fun onFirebaseConfig() {
        val mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val mFirebaseRemoteConfigSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds((60 * 10).toLong())
            .build()
        mFirebaseRemoteConfig.setConfigSettingsAsync(mFirebaseRemoteConfigSettings)
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        mFirebaseRemoteConfig.fetch(3600)
            .addOnCompleteListener(
                this
            ) { task: Task<Void?> ->
                if (task.isSuccessful) {
                    mFirebaseRemoteConfig.fetchAndActivate()
                    val appVersionCode =
                        mFirebaseRemoteConfig.getString("appVersionCode")
                    compareVersion(appVersionCode)
                }
            }
    }

    /** 2021.11.01 버전 비교  */
    private fun compareVersion(appVersionCode: String) {
        val thisAppVersionCode: Int = getAppVersionCode()
        Log.d("TEST", "thisAppVersionCode: $thisAppVersionCode")
        Log.d("TEST", "appVersionCode: $appVersionCode")
        if (TextUtils.isEmpty(appVersionCode)) {
//            checkLoginInfo()
            return
        }
        if (thisAppVersionCode < appVersionCode.toInt()) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.txt_app_update))
                .setMessage(getString(R.string.txt_app_update_need))
                .setPositiveButton(getString(R.string.txt_app_update)) { dialog: DialogInterface?, which: Int ->
                    val marketLaunch = Intent(Intent.ACTION_VIEW)
                    marketLaunch.data =
                        Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                    //                        marketLaunch.setData(Uri.parse("market://details?id=" + getPackageName()));
                    startActivity(marketLaunch)
                    finish()
                }
                .setNegativeButton(
                    getString(R.string.action_cancel)
                ) { dialog: DialogInterface?, which: Int -> finish() }
            val alertDialog = builder.create()
            alertDialog.show()
        } else {
//            checkLoginInfo()
        }
    }

    private fun getAppVersion(context: Context): String? {
        return try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            "1.0.0"
        }
    }

    private fun getAppVersionCode(): Int {
        return try {
            val pInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_META_DATA)
            pInfo.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            1
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // facebook 인증 처리
        if (viewModel.callbackManager != null) {
            viewModel.callbackManager!!.onActivityResult(requestCode, resultCode, data)
        }
    }
}