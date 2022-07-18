/*
 * Create by jhong on 2022. 7. 11.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.ui.intro

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorizava.asrplayer.config.LoginManager
import com.sorizava.asrplayer.data.IntroState
import com.sorizava.asrplayer.data.ResultState
import com.sorizava.asrplayer.data.vo.LoginDataVO
import com.sorizava.asrplayer.data.vo.LoginNewRequest
import com.sorizava.asrplayer.extension.getVersion
import com.sorizava.asrplayer.network.AppApiResponse
import com.sorizava.asrplayer.repository.LoginRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.mozilla.focus.ext.application

private const val MAX_DELAY_TIME_WITH_INTRO = 3000L

class IntroViewModel(private val context: Application) : ViewModel() {

    private val validAppVersionPrivate = MutableLiveData<IntroState>()
    val validAppVersion: LiveData<IntroState> = validAppVersionPrivate

    private val appVersionPrivate = MutableLiveData<String>()
    val appVersion: LiveData<String> = appVersionPrivate

    private val isValidLoginPrivate = MutableLiveData<Boolean>()
    val isValidLogin: LiveData<Boolean> = isValidLoginPrivate

    private val needLoginPrivate = MutableLiveData<Boolean>()
    val needLogin: LiveData<Boolean> = needLoginPrivate

    init {

        appVersionPrivate.value = context.getVersion()

        viewModelScope.launch {
            validAppVersionPrivate.value = IntroState.LOADING

            val time = System.currentTimeMillis()

            if (!isLatestVersion()) {
                validAppVersionPrivate.value = IntroState.NEED_APP_UPDATE
            } else {
                val delayTime = (System.currentTimeMillis() - time)

                if (MAX_DELAY_TIME_WITH_INTRO > delayTime) {
                    delay(MAX_DELAY_TIME_WITH_INTRO - delayTime)
                }

                validAppVersionPrivate.value = IntroState.CHECK_LOGIN
            }
        }
    }

    private fun isLatestVersion(): Boolean {
        return this.context.application.isLatestVersion()
    }

    fun checkLoginInfo() {
        viewModelScope.launch {

            val birth = LoginManager.instance?.prefUserBirth
            val phone = LoginManager.instance?.prefUserPhone

            if (LoginManager.instance?.userSNSType == LoginManager.SNS_TYPE_NONE) {
                validAppVersionPrivate.value = IntroState.GOTO_MAIN
            } else if (TextUtils.isEmpty(birth)) {
                validAppVersionPrivate.value = IntroState.GOTO_MAIN
            } else {
                if (birth != null && phone != null){
                    val request = LoginNewRequest(birth, phone)
                    val repository = LoginRepository(context.application, request)
                    repository.requestMemberInfo().collect {
                        when (it) {
                            ResultState.Success(data = AppApiResponse<LoginDataVO>()) -> {

                            }

                            else -> {

                            }
                        }
                        validAppVersionPrivate.value = IntroState.GOTO_LOGIN
                    }
                }
            }
        }
    }
}
