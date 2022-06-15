package org.mozilla.focus.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import kr.co.sorizava.asrplayer.AppConfig;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        getHashKey();

        // 시작 통계 삭제
        AppConfig.getInstance().clearPrefstatistics();

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

        startActivity(new Intent(this, IntroActivity.class));
//        startActivity(new Intent(this, MainActivity.class));
//        startActivity(new Intent(this, Signup2Activity.class));
        finish();
    }

    @SuppressLint("PackageManagerGetSignatures")
    private void getHashKey(){
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null) {
            Log.e("KeyHash", "KeyHash:null");
        } else {
            for (Signature signature : packageInfo.signatures) {
                try {
                    MessageDigest md = MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
                    Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                } catch (NoSuchAlgorithmException e) {
                    Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
                }
            }
        }
    }
}