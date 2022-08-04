/*
 * Create by jhong on 2022. 1. 10.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.config

import android.annotation.SuppressLint
import android.content.ContentUris
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Looper
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media
import androidx.annotation.ChecksSdkIntAtLeast
import org.mozilla.focus.R

/** # 앱에서 사용하는 모든 상수 값 모음 */

const val TAG = "EARZOOM"

const val PREFS_KEY_FILE = "PrefsEarzoom.sz"
const val PREFS_KEY = "PrefsEarzoom"

const val APP_ID = "app_id"

/** 로그인 화면 진입시 UI 상태를 나타내는 flag 값을 의미
 * relogin type은 로그인을 다시 해야하는 사항이 발생하여 로그인 화면을
 *
 * */
const val LOGIN_TYPE_RELOGIN = "relogin"


const val NAVER_CLIENT_ID = "naver_client_id"
const val NAVER_CLIENT_SECRET = "naver_client_secret"
const val NAVER_CLIENT_NAME = "naver_client_name"


const val EARZOOM_BOARD_BASE_URL = "http://211.248.153.107:1969"

const val API_NOTICE_URL = "api/board/noticeView"
const val API_FAQ_URL = "api/board/faqView"
const val API_EVENT_URL = "api/board/eventView"











const val SETTINGS_CHANGE_HOUR_FORMAT = "settings_change_hour_format"
const val SETTINGS_CHANGE_DATE_TIME_FORMAT = "settings_change_date_time_format"

const val SECURITY_KEY = "security_key"
// security TODO - 향후 변경
const val SECRET_KEY = "sorizava_dev_security_220321"


// permissions
const val PERMISSION_READ_STORAGE = 1
const val PERMISSION_WRITE_STORAGE = 2
const val PERMISSION_RECORD_AUDIO = 3
const val PERMISSION_PHONE_STATE = 4
const val PERMISSION_MANAGE_EXTERNAL_STORAGE = 10

const val GENERIC_PERM_HANDLER = 1580

val BITRATES = arrayListOf(32000, 64000, 96000, 128000, 160000, 192000, 256000, 320000)
const val DEFAULT_BITRATE = 128000

// shared preferences
const val HIDE_NOTIFICATION = "hide_notification"
const val SAVE_RECORDINGS = "save_recordings"
const val EXTENSION = "extension"
const val BITRATE = "bitrate"
const val RECORD_AFTER_LAUNCH = "record_after_launch"
const val SD_TREE_URI = "tree_uri_2"
const val ENCRYPTION = "encryption"

const val WIDGET_BG_COLOR = "widget_bg_color"
const val WIDGET_TEXT_COLOR = "widget_text_color"
val DEFAULT_WIDGET_BG_COLOR = Color.parseColor("#AA000000")

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.R)
fun isRPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.Q)
fun isQPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
fun isOreoPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

fun isOnMainThread() = Looper.myLooper() == Looper.getMainLooper()
