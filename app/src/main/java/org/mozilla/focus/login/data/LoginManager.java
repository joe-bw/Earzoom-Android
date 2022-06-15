package org.mozilla.focus.login.data;

import android.content.Context;
import android.content.SharedPreferences;

public class LoginManager {
    private final String KEY_LOGIN_AUTO = "isloginAuto";
    private final String KEY_USER_SNS_ID = "userSNSId";
    private final String KEY_USER_SNS_TYPE = "userSNSType";

    private final String KEY_DEVICE_TOKEN = "deviceToken";

    /** 2021.10.31 생년월일, 폰번호 저장 pref */
    private final String PREF_KEY_USER_BIRTH = "PREF_KEY_USER_BIRTH";
    private final String PREF_KEY_USER_PHONE = "PREF_KEY_USER_PHONE";

    /** 2021.11.01 웹서버 ID pref */
    private final String PREF_KEY_USER_ID = "PREF_KEY_USER_ID";

    public static final int SNS_TYPE_NONE = -1;
    public static final int SNS_TYPE_NAVER = 0;
    public static final int SNS_TYPE_KAKAO = 1;
    public static final int SNS_TYPE_FACEBOOK = 2;
    public static final int SNS_TYPE_GOOGLE = 3;

    private static LoginManager instance = null;

    public static LoginManager getInstance() {
        return instance;
    }

    private SharedPreferences app_prefs;

    public LoginManager(Context applicationContext) {
        this.app_prefs = applicationContext.getSharedPreferences("shared", 0);
    }

    public static void onInit(Context applicationContext) {
        if (instance == null) {
            instance = new LoginManager(applicationContext);
        }
    }

    public void clear() {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.remove(KEY_USER_SNS_ID);
        edit.remove(KEY_USER_SNS_TYPE);
        edit.remove(PREF_KEY_USER_BIRTH);
        edit.remove(PREF_KEY_USER_PHONE);
        edit.remove(PREF_KEY_USER_ID);
        edit.commit();
    }

    public void putIsLoginAuto(boolean loginOrOut) {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putBoolean(KEY_LOGIN_AUTO, loginOrOut);
        edit.apply();
    }

    public boolean isLoginAuto() {
        return app_prefs.getBoolean(KEY_LOGIN_AUTO, false);
    }

    public void putUserId(String id) {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString(KEY_USER_SNS_ID, id);
        edit.apply();
    }

    public String getUserId() {
        return app_prefs.getString(KEY_USER_SNS_ID, "");
    }

    public void putUserSNSType(int type) {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putInt(KEY_USER_SNS_TYPE, type);
        edit.apply();
    }

    public int getUserSNSType() {
        return app_prefs.getInt(KEY_USER_SNS_TYPE, SNS_TYPE_NONE);
    }


    public void putDeviceToken(String token) {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString(KEY_DEVICE_TOKEN, token);
        edit.apply();
    }

    public String getDeviceToken() {
        return app_prefs.getString(KEY_DEVICE_TOKEN, "device_token_test");
    }


    /** 2021.10.31 생년월일 pref */
    public String getPrefUserBirth() {
        return app_prefs.getString(PREF_KEY_USER_BIRTH, "");
    }

    public void setPrefUserBirth(String date) {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString(PREF_KEY_USER_BIRTH, date);
        edit.apply();
    }

    /** 2021.10.31 폰번호 pref */
    public String getPrefUserPhone() {
        return app_prefs.getString(PREF_KEY_USER_PHONE, "");
    }

    public void setPrefUserPhone(String phone) {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString(PREF_KEY_USER_PHONE, phone);
        edit.apply();
    }

    /** 2021.10.31 폰번호 pref */
    public String getPrefUserId() {
        return app_prefs.getString(PREF_KEY_USER_ID, "");
    }

    public void setPrefUserId(String id) {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString(PREF_KEY_USER_ID, id);
        edit.apply();
    }
}
