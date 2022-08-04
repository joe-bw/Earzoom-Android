/*
 * Create by jhong on 2022. 7. 7.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */
package com.sorizava.asrplayer

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.sorizava.asrplayer.ui.intro.IntroActivity
import kr.co.sorizava.asrplayer.AppConfig
import org.mozilla.focus.activity.MainActivity
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        getHashKey()

        // 시작 통계 삭제
        AppConfig.getInstance().clearPrefstatistics()

        // 페이스북 빠른 로그인
//        LoginManager.getInstance().retrieveLoginStatus(this, new LoginStatusCallback() {
//            @Override public void onCompleted(AccessToken accessToken) {
//                // User was previously logged in, can log them in directly here. // If this callback is called, a popup notification appears that says // "Logged in as <User Name>"
//                } @Override public void onFailure() {
//                // No access token could be retrieved for the user
//                } @Override public void onError(Exception exception) {
//                // An error occurred
//            }
//        });

//        startActivity(Intent(this, SampleMainActivity::class.java))
//        startActivity(Intent(this, IntroActivity::class.java))
                startActivity(Intent(this, MainActivity::class.java))
//        startActivity(new Intent(this, Signup2Activity.class));
        finish()
    }

    /** KeyHash 값을 얻는 코드, Facebook, Firebase 등에서 앱을 등룍하는 key */
    private fun getHashKey() {
        var packageInfo: PackageInfo? = null
        try {
            packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        Log.e("KeyHash", "getHashKey()")

        if (packageInfo == null) {
            Log.e("KeyHash", "KeyHash:null")
        } else {
            for (signature in packageInfo.signatures) {
                try {
                    val md = MessageDigest.getInstance("SHA")
                    md.update(signature.toByteArray())
                    Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT))
                } catch (e: NoSuchAlgorithmException) {
                    Log.e("KeyHash", "Unable to get MessageDigest. signature=$signature", e)
                }
            }
        }
    }
}