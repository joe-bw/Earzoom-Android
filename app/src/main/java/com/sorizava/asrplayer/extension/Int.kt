/*
 * Create by jhong on 2022. 2. 14.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.extension

import android.content.res.Resources
import android.text.format.DateFormat
import java.util.Locale
import java.util.Calendar

fun Int.dp(): Float {
    return this * Resources.getSystem().displayMetrics.density
}

fun Int.pxTodp(): Float {

    var density = Resources.getSystem().displayMetrics.density

    if (density == 1.0f) // mpdi (160dpi) -- xxxhdpi (density = 4)기준으로 density 값을 재설정 한다
        density *= 4.0f
    else if (density == 1.5f) // hdpi (240dpi)
        density *= (8 / 3)
    else if (density == 2.0f) // xhdpi (320dpi)
        density *= 2.0f

    return this / density
}

fun Int.getFormattedDuration(forceShowHours: Boolean = true): String {
    val sb = StringBuilder(8)
    val hours = this / 3600
    val minutes = this % 3600 / 60
    val seconds = this % 60

    if (this >= 3600) {
        sb.append(String.format(Locale.getDefault(), "%02d", hours)).append(":")
    } else if (forceShowHours) {
        sb.append("00:")
    }
    sb.append(String.format(Locale.getDefault(), "%02d", minutes))
    sb.append(":").append(String.format(Locale.getDefault(), "%02d", seconds))
    return sb.toString()
}

fun Int.formatDate(): String {
    val useDateFormat = "yyyy.MM.dd EEE a hh:mm"
    val cal = Calendar.getInstance(Locale.KOREA)
    cal.timeInMillis = this * 1000L
    return DateFormat.format(useDateFormat, cal).toString()
}
