/*
 * Create by jhong on 2022. 7. 11.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.ui.intro

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorizava.asrplayer.config.LoginManager
import com.sorizava.asrplayer.data.IntroCode
import com.sorizava.asrplayer.extension.getVersion
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.mozilla.focus.FocusApplication

private const val MAX_DELAY_TIME_WITH_INTRO = 3000L

class IntroViewModel(application: Application) : ViewModel() {

    private val validAppVersionPrivate = MutableLiveData<IntroCode>()
    val validAppVersion: LiveData<IntroCode> = validAppVersionPrivate

    private val appVersionPrivate = MutableLiveData<String>()
    val appVersion: LiveData<String> = appVersionPrivate

    private val isValidLoginPrivate = MutableLiveData<Boolean>()
    val isValidLogin: LiveData<Boolean> = isValidLoginPrivate

    init {
        appVersionPrivate.value = application.getVersion()

        viewModelScope.launch {
            validAppVersionPrivate.value = IntroCode.LOADING

            val time = System.currentTimeMillis()
            // TODO : do something

            val valid = getFirebaseConfigInfo()

            if (valid) {
                validAppVersionPrivate.value = IntroCode.NEED_APP_UPDATE
            } else {
                val delayTime = (System.currentTimeMillis() - time)

                if (MAX_DELAY_TIME_WITH_INTRO > delayTime) {
                    delay(MAX_DELAY_TIME_WITH_INTRO - delayTime)
                }

                validAppVersionPrivate.value = IntroCode.GOTO_MAIN
            }
        }
    }

    private fun getFirebaseConfigInfo(): Boolean {
        return FocusApplication().isLatestVersion()
    }

    fun checkLoginInfo() {
        viewModelScope.launch {
            if (LoginManager.instance?.userSNSType == LoginManager.SNS_TYPE_NONE) {

            } else {

            }
        }
    }


}