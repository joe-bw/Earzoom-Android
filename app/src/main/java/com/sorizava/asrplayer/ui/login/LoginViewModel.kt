/*
 * Create by jhong on 2022. 7. 18.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.ui.login

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorizava.asrplayer.data.ResultState
import com.sorizava.asrplayer.data.SnsProvider
import com.sorizava.asrplayer.data.SnsResult
import com.sorizava.asrplayer.repository.SnsLoginRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LoginViewModel(private val context: Application) : ViewModel() {

    private val signCallbackPrivate = MutableLiveData<ResultState<SnsResult>>()
    val signCallback: LiveData<ResultState<SnsResult>> = signCallbackPrivate

    private var repository: SnsLoginRepository = SnsLoginRepository(context)

    fun signIn(provider: SnsProvider) {
        viewModelScope.launch {
            signCallbackPrivate.value = ResultState.Loading()
            repository.signIn(provider).collect {
                signCallbackPrivate.value = it
            }
        }
    }
}


