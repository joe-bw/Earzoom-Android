/*
 * Create by jhong on 2022. 6. 3.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.ui.main

import com.sorizava.asrplayer.ui.base.ItemSelectedListener

interface BookmarkItemSelectedListener<T> {

    fun onSelectedItem(item: T, mode: Boolean)
    fun onLongSelectedItem(item: T, mode: Boolean)
}