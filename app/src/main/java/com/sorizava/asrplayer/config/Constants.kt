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

/** # 앱에서 사용하는 모든 상수 값 모음 */

const val TAG = "BARO_NOTE"

const val PREFS_KEY_FILE = "PrefsBaroNote.sz"
const val PREFS_KEY = "PrefsBaroNote"

const val APP_ID = "app_id"

const val BARONOTE_PATH_NAME = "Baronote"
const val BARONOTE_PATH_DEFAULT_NAME = "기본폴더"

const val SD_CARD_PATH = "sd_card_path"
const val BARONOTE_PATH = "baronote_path"
const val CURRENT_PATH = "current_path"
const val CURRENT_MEDIA_PATH = "current_media_path"
const val INTERNAL_STORAGE_PATH = "internal_storage_path"

const val SETTINGS_CHANGE_HOUR_FORMAT = "settings_change_hour_format"
const val SETTINGS_CHANGE_DATE_TIME_FORMAT = "settings_change_date_time_format"

const val SECURITY_KEY = "security_key"
// security TODO - 향후 변경
const val SECRET_KEY = "sorizava_dev_security_220321"

// sorting
const val SORT_ORDER = "sort_order"
const val SORT_FOLDER_PREFIX = "sort_folder_"
const val SORT_BY_NAME = 1
const val SORT_BY_DATE_MODIFIED = 2
const val SORT_BY_SIZE = 4
const val SORT_BY_EXTENSION = 16
const val SORT_DESCENDING = 1024

// permissions
const val PERMISSION_READ_STORAGE = 1
const val PERMISSION_WRITE_STORAGE = 2
const val PERMISSION_RECORD_AUDIO = 3
const val PERMISSION_PHONE_STATE = 4
const val PERMISSION_MANAGE_EXTERNAL_STORAGE = 10

const val GENERIC_PERM_HANDLER: Int = 1580

// record
const val RECORDER_RUNNING_NOTIF_ID = 10000
private const val prefix = "kr.co.sorizava.baronote.android.action."

const val START_FOREGROUND = prefix + "START_FOREGROUND"
const val STOP_FOREGROUND = prefix + "STOP_FOREGROUND"
const val STOP_RESTART_FOREGROUND = prefix + "STOP_RESTART_FOREGROUND"

const val GET_RECORDER_INFO = prefix + "GET_RECORDER_INFO"
const val UPDATE_AMPLITUDE = prefix + "UPDATE_AMPLITUDE"
const val UPDATE_DURATION = prefix + "UPDATE_DURATION"
const val TOGGLE_PAUSE = prefix + "TOGGLE_PAUSE"
const val NOT_AVAILABLE_SPACES = prefix + "NOT_AVAILABLE_SPACES"
const val STATUS_RECORDING = prefix + "STATUS_RECORDING"
const val CANCEL_RECORDING = prefix + "CANCEL_RECORDING"
const val CHANGE_FILE_NAME = prefix + "CHANGE_FILE_NAME"
const val COMPLETED_RECORDING = prefix + "COMPLETED_RECORDING"
const val PHONE_STATE_PREFIX = "android.intent.action.PHONE_STATE"
const val STREAM_DEVICES_CHANGED_ACTION_PREFIX = "android.media.STREAM_DEVICES_CHANGED_ACTION"
const val ALERT_CHANGED_ACTION_PREFIX = "android.app.action.NEXT_ALARM_CLOCK_CHANGED"

const val MSG_DATA = prefix + "MSG_DATA"

const val RECORDING_RUNNING = 0
const val RECORDING_STOPPED = 1
const val RECORDING_PAUSED = 2
const val RECORDING_CONTINUED = 3

const val EXTENSION_M4A = 0
const val EXTENSION_MP3 = 1
const val EXTENSION_OGG = 2

val BITRATES = arrayListOf(32000, 64000, 96000, 128000, 160000, 192000, 256000, 320000)
const val DEFAULT_BITRATE = 128000

// 장시간 녹음시 제한 사항으로, 제한시간은 2시간
const val RECORDING_LIMIT_TIME = 7200

// 최소 녹음 시간 현재 600 초
const val MIN_AVAILABLE_RECORDING_TIME: Long = 600 * 1000L // 1000 X 600 = 10 Minute
const val MIN_REMAIN_RECORDING_TIME: Long = 60 * 1000L // 1000 X 60 = 1 Minute

const val IS_RECORDING = "is_recording"
const val TOGGLE_WIDGET_UI = "toggle_widget_ui"

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

// Intent extra name
const val SEARCH_ITEM = "SEARCH_ITEM"
const val RECORD_ITEM = "RECORD_ITEM"
const val RECORD_ITEM_URI = "RECORD_ITEM_URI"

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.R)
fun isRPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.Q)
fun isQPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
fun isOreoPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

val photoExtensions: Array<String>
    get() = arrayOf(
        ".jpg",
        ".png",
        ".jpeg",
        ".bmp",
        ".webp",
        ".heic",
        ".heif"
    )
val videoExtensions: Array<String>
    get() = arrayOf(
        ".mp4",
        ".mkv",
        ".webm",
        ".avi",
        ".3gp",
        ".mov",
        ".m4v",
        ".3gpp"
    )
val audioExtensions: Array<String>
    get() = arrayOf(
        ".mp3",
        ".wav",
        ".wma",
        ".ogg",
        ".m4a",
        ".opus",
        ".flac",
        ".aac"
    )

const val DATE_FORMAT_ONE = "dd.MM.yyyy"
const val DATE_FORMAT_TWO = "dd/MM/yyyy"
const val DATE_FORMAT_THREE = "MM/dd/yyyy"
const val DATE_FORMAT_FOUR = "yyyy-MM-dd"
const val DATE_FORMAT_FIVE = "d MMMM yyyy"
const val DATE_FORMAT_SIX = "MMMM d yyyy"
const val DATE_FORMAT_SEVEN = "MM-dd-yyyy"
const val DATE_FORMAT_EIGHT = "dd-MM-yyyy"

const val TIME_FORMAT_12 = "hh:mm a"
const val TIME_FORMAT_24 = "HH:mm"

const val encryptionExtension: String = "aes"
const val encryptionExtensionDotted: String = ".$encryptionExtension"

// message digest algorithms
const val MD5 = "MD5"
const val SHA1 = "SHA-1"
const val SHA256 = "SHA-256"
const val SHA512 = "SHA-512"

fun isOnMainThread() = Looper.myLooper() == Looper.getMainLooper()

fun ensureBackgroundThread(callback: () -> Unit) {
    if (isOnMainThread()) {
        Thread {
            callback()
        }.start()
    } else {
        callback()
    }
}

@SuppressLint("InlinedApi")
fun getAudioFileContentUri(id: Long): Uri {
    val baseUri = if (isQPlus()) {
        Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    } else {
        Media.EXTERNAL_CONTENT_URI
    }

    return ContentUris.withAppendedId(baseUri, id)
}
