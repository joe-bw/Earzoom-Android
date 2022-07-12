/*
 * Create by jhong on 2022. 3. 4.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

/** API 30 을 기준으로 구분하여 permission 처리
 *
 * API 30 이상 - Write storage permission 이 필요 없음
 * API 30 미만 - Write storage permission 이 필요
 *
 * */
class PermissionManager {

    private val permissions = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_PHONE_STATE,
    )

    fun checkPermissionsGranted(context: Context?): Boolean {
        for (permission in permissions) {
            if (!checkSelfPermissions(context!!, permission)) {
                return false
            }
        }
        return true
    }

    private fun checkSelfPermissions(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission(activity: Activity, requestCode: Int) {
        activity.requestPermissions(permissions, requestCode)
    }

//    fun requestPermission(fragment: Fragment, requestCode: Int) {
//        fragment.requestPermissions(permissions, requestCode)
//    }
}
