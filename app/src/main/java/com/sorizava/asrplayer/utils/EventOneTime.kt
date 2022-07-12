/*
 * Create by jhong on 2022. 2. 14.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

open class EventOneTime<T>(value: T) {

    var value = value
        private set

    private var isAlreadyHandled = false

    fun isActive(): Boolean = if (isAlreadyHandled) {
        false
    } else {
        isAlreadyHandled = true
        true
    }
}

fun <T> LiveData<EventOneTime<T>>.observeEvent(owner: LifecycleOwner, observer: Observer<T>) = observe(owner) {
    if (it.isActive()) {
        observer.onChanged(it.value)
    }
}

fun MutableLiveData<EventOneTime<Unit>>.emit() = postValue(EventOneTime(Unit))

fun <T> MutableLiveData<EventOneTime<T>>.emit(value: T) = postValue(EventOneTime(value))
