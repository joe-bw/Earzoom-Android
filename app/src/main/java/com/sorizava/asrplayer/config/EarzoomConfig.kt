/*
 * Create by jhong on 2022. 1. 12.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.config

import android.annotation.SuppressLint
import android.content.Context
import android.text.format.DateFormat
import com.sorizava.asrplayer.extension.getSharedPrefs

/** ## 설정 클래스 */
open class EarzoomConfig(val context: Context) {

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: EarzoomConfig? = null

        fun getInstance(context: Context): EarzoomConfig {
            return instance ?: synchronized(this) {
                instance ?: EarzoomConfig(context).also {
                    instance = it
                }
            }
        }
    }

    private val prefs = context.getSharedPrefs()

//    var currentMediaName: String
//        get() = prefs.getString(CURRENT_MEDIA_PATH, BARONOTE_PATH_DEFAULT_NAME)!!
//        set(path) = prefs.edit().putString(CURRENT_MEDIA_PATH, path).apply()

//    private fun getDefaultSDCardPath() =
//        if (prefs.contains(SD_CARD_PATH)) "" else context.getSDCardPath()

    var use24HourFormat: Boolean
        get() = prefs.getBoolean(
            SETTINGS_CHANGE_HOUR_FORMAT,
            DateFormat.is24HourFormat(context)
        )
        set(use24HourFormat) = prefs.edit()
            .putBoolean(SETTINGS_CHANGE_HOUR_FORMAT, use24HourFormat).apply()

    var appId: String
        get() = prefs.getString(APP_ID, "")!!
        set(appId) = prefs.edit().putString(APP_ID, appId).apply()

    var hideNotification: Boolean
        get() = prefs.getBoolean(HIDE_NOTIFICATION, false)
        set(hideNotification) = prefs.edit().putBoolean(HIDE_NOTIFICATION, hideNotification).apply()

    var sdTreeUri: String
        get() = prefs.getString(SD_TREE_URI, "")!!
        set(uri) = prefs.edit().putString(SD_TREE_URI, uri).apply()

    var bitrate: Int
        get() = prefs.getInt(BITRATE, DEFAULT_BITRATE)
        set(bitrate) = prefs.edit().putInt(BITRATE, bitrate).apply()

    var widgetBgColor: Int
        get() = prefs.getInt(WIDGET_BG_COLOR, DEFAULT_WIDGET_BG_COLOR)
        set(widgetBgColor) = prefs.edit().putInt(WIDGET_BG_COLOR, widgetBgColor).apply()

    var secretKey: String
        get() = prefs.getString(SECURITY_KEY, SECRET_KEY)!!
        set(key) = prefs.edit().putString(SECURITY_KEY, key).apply()

    var isEncryption: Boolean
        get() = prefs.getBoolean(ENCRYPTION, false)
        set(value) = prefs.edit().putBoolean(ENCRYPTION, value).apply()
}
