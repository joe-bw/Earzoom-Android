/*
 * Create by jhong on 2022. 7. 5.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorizava.asrplayer.data.ResultState
import kotlinx.coroutines.flow.*

class MainUrlViewModel : ViewModel() {


    private val homeContentsPrivate = MutableLiveData<String>()
    val homeContents: LiveData<String> get() = homeContentsPrivate

    val uiState: StateFlow<ResultState<String>> = requestName()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = ResultState.Loading()
        )


    init {



        homeContentsPrivate.value = "TEST 1235"

    }

    private fun requestName() : Flow<ResultState<String>> {
        return flow {
            emit(ResultState.Success("TEST 123"))
        }
    }
}
