/*
 * Create by jhong on 2022. 7. 5.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorizava.asrplayer.data.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class HomeViewModel : ViewModel() {


    private val homeContentsPrivate = MutableLiveData<String>()
    val homeContents: LiveData<String> get() = homeContentsPrivate

    val uiState: StateFlow<UiState<String>> = requestName()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = UiState.Loading()
        )


    init {



        homeContentsPrivate.value = "TEST 1235"

    }

    private fun requestName() : Flow<UiState<String>> {
        return flow {
            emit(UiState.Success("TEST 123"))
        }
    }
}
