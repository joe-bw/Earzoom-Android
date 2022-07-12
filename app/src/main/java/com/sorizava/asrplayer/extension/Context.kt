/*
 * Create by jhong on 2022. 1. 12.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.extension

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.sorizava.asrplayer.config.*

// with (sharedPreferences.edit()) {
//    // Edit the user's shared preferences...
//    apply()
// }


fun Context.getSharedPrefs(): SharedPreferences =
    getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)

val Context.config: EarzoomConfig get() = EarzoomConfig.getInstance(applicationContext)

fun Context.getVersion(): String =
    this.packageManager.getPackageInfo(this.packageName, 0).versionName

fun Context.hasPermission(permId: Int) = ContextCompat.checkSelfPermission(
    this,
    getPermissionString(permId)
) == PackageManager.PERMISSION_GRANTED

fun getPermissionString(id: Int) = when (id) {
    PERMISSION_RECORD_AUDIO -> Manifest.permission.RECORD_AUDIO
    PERMISSION_READ_STORAGE -> Manifest.permission.READ_EXTERNAL_STORAGE
    PERMISSION_WRITE_STORAGE -> Manifest.permission.WRITE_EXTERNAL_STORAGE
    PERMISSION_PHONE_STATE -> Manifest.permission.READ_PHONE_STATE
    else -> ""
}

fun Context.toast(id: Int, length: Int = Toast.LENGTH_SHORT): Toast? = toast(getString(id), length)

fun Context.toast(msg: String, length: Int = Toast.LENGTH_SHORT): Toast? {
    try {
        if (isOnMainThread()) {
            return doToast(this, msg, length)
        } else {
            Handler(Looper.getMainLooper()).post {
                doToast(this, msg, length)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

private fun doToast(context: Context, message: String, length: Int): Toast? {
    val toast = Toast.makeText(context, message, length)
    if (context is Activity) {
        if (!context.isFinishing && !context.isDestroyed) {
            toast.show()
            return toast
        }
    } else {
        toast.show()
        return toast
    }
    return null
}

fun Context.getLaunchIntent() = packageManager.getLaunchIntentForPackage(config.appId)

fun Context.drawableToBitmap(drawable: Drawable): Bitmap {
    val size = (60 * resources.displayMetrics.density).toInt()
    val mutableBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(mutableBitmap)
    drawable.setBounds(0, 0, size, size)
    drawable.draw(canvas)
    return mutableBitmap
}


val Context.connectivityManager: ConnectivityManager
    get() =
        this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
