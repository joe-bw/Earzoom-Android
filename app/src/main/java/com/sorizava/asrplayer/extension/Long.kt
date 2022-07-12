package com.sorizava.asrplayer.extension

import android.text.format.DateFormat
import java.text.DecimalFormat
import java.util.Locale
import java.util.Calendar
import kotlin.math.log10
import kotlin.math.pow

fun Long.formatSize(): String {
    if (this <= 0) {
        return "0 B"
    }

    val units = arrayOf("B", "kB", "MB", "GB", "TB")
    val digitGroups = (log10(toDouble()) / log10(1024.0)).toInt()
    return "${DecimalFormat("#,##0.#").format(
        this / 1024.0.pow(digitGroups.toDouble())
    )} ${units[digitGroups]}"
}

/** 폴더와 파일의 상세 날짜 format */
fun Long.formatDate(): String {
    val useDateFormat = "yyyy.MM.dd EEE a hh:mm"
    val cal = Calendar.getInstance(Locale.KOREA)
    cal.timeInMillis = this
    return DateFormat.format(useDateFormat, cal).toString()
}
