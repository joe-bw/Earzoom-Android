/*
 * Create by jhong on 2022. 7. 7.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */
package com.sorizava.asrplayer.config

import android.content.Context
import android.content.SharedPreferences


private const val KEY_LOGIN_AUTO = "isloginAuto"
private const val KEY_USER_SNS_ID = "userSNSId"
private const val KEY_USER_SNS_TYPE = "userSNSType"
private const val KEY_DEVICE_TOKEN = "deviceToken"

/** 2021.10.31 생년월일, 폰번호 저장 pref  */
private const val PREF_KEY_USER_BIRTH = "PREF_KEY_USER_BIRTH"
private const val PREF_KEY_USER_PHONE = "PREF_KEY_USER_PHONE"

/** 2021.11.01 웹서버 ID pref  */
private const val PREF_KEY_USER_ID = "PREF_KEY_USER_ID"

class SorizavaLoginManager(applicationContext: Context) {
    private val app_prefs: SharedPreferences
    fun clear() {
        val edit = app_prefs.edit()
        edit.remove(KEY_USER_SNS_ID)
        edit.remove(KEY_USER_SNS_TYPE)
        edit.remove(PREF_KEY_USER_BIRTH)
        edit.remove(PREF_KEY_USER_PHONE)
        edit.remove(PREF_KEY_USER_ID)
        edit.commit()
    }

    fun putIsLoginAuto(loginOrOut: Boolean) {
        val edit = app_prefs.edit()
        edit.putBoolean(KEY_LOGIN_AUTO, loginOrOut)
        edit.apply()
    }

    val isLoginAuto: Boolean
        get() = app_prefs.getBoolean(KEY_LOGIN_AUTO, false)

    fun putUserId(id: String?) {
        val edit = app_prefs.edit()
        edit.putString(KEY_USER_SNS_ID, id)
        edit.apply()
    }

    val userId: String?
        get() = app_prefs.getString(KEY_USER_SNS_ID, "")

    fun putUserSNSType(type: Int) {
        val edit = app_prefs.edit()
        edit.putInt(KEY_USER_SNS_TYPE, type)
        edit.apply()
    }

    val userSNSType: Int
        get() = app_prefs.getInt(KEY_USER_SNS_TYPE, SNS_TYPE_NONE)

    fun putDeviceToken(token: String?) {
        val edit = app_prefs.edit()
        edit.putString(KEY_DEVICE_TOKEN, token)
        edit.apply()
    }

    val deviceToken: String?
        get() = app_prefs.getString(KEY_DEVICE_TOKEN, "device_token_test")

    /** 2021.10.31 생년월일 pref  */
    var prefUserBirth: String?
        get() = app_prefs.getString(PREF_KEY_USER_BIRTH, "")
        set(date) {
            val edit = app_prefs.edit()
            edit.putString(PREF_KEY_USER_BIRTH, date)
            edit.apply()
        }

    /** 2021.10.31 폰번호 pref  */
    var prefUserPhone: String?
        get() = app_prefs.getString(PREF_KEY_USER_PHONE, "")
        set(phone) {
            val edit = app_prefs.edit()
            edit.putString(PREF_KEY_USER_PHONE, phone)
            edit.apply()
        }

    /** 2021.10.31 폰번호 pref  */
    var prefUserId: String?
        get() = app_prefs.getString(PREF_KEY_USER_ID, "")
        set(id) {
            val edit = app_prefs.edit()
            edit.putString(PREF_KEY_USER_ID, id)
            edit.apply()
        }

    companion object {
        const val SNS_TYPE_NONE = -1
        const val SNS_TYPE_NAVER = 0
        const val SNS_TYPE_KAKAO = 1
        const val SNS_TYPE_FACEBOOK = 2
        const val SNS_TYPE_GOOGLE = 3
        @JvmStatic
        var instance: SorizavaLoginManager? = null
            private set

        fun onInit(applicationContext: Context) {
            if (instance == null) {
                instance = SorizavaLoginManager(applicationContext)
            }
        }
    }

    init {
        app_prefs = applicationContext.getSharedPreferences("shared", 0)
    }
}