/*
 * Create by jhong on 2022. 7. 5.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.ui.main

import android.app.Application
import androidx.lifecycle.*
import com.sorizava.asrplayer.data.item.BookmarkItem
import kotlinx.coroutines.launch
import org.mozilla.focus.R

/** 현재는 테스트로 context 를 사용하고 있는데 이를 제거하도록 한다. */
class MainBookmarkViewModel(application: Application) : AndroidViewModel(application) {

    private val context by lazy { application.applicationContext }

    var isEditMode: Boolean = false

    private val bookmarkListPrivate = MutableLiveData<ArrayList<BookmarkItem>>()
    val bookmarkList: LiveData<ArrayList<BookmarkItem>> get() = bookmarkListPrivate

    fun requestBookmarkList() {
        viewModelScope.launch {
            val list = testTempData()
            list.add(BookmarkItem(R.drawable.ic_plus_48, "", "", true))
            bookmarkListPrivate.value = list
        }
    }

    private fun testTempData(): ArrayList<BookmarkItem> {

        val list = arrayListOf<BookmarkItem>()

        list.add(BookmarkItem(R.drawable.ic_main_kbs, context.resources.getString(R.string.kbs), "", false))
        list.add(BookmarkItem(R.drawable.ic_main_mbc, context.resources.getString(R.string.mbc), "", false))
        list.add(BookmarkItem(R.drawable.ic_main_sbs, context.resources.getString(R.string.sbs), "", false))
        list.add(BookmarkItem(R.drawable.ic_main_ytn, context.resources.getString(R.string.ytn), "", false))
        list.add(BookmarkItem(R.drawable.ic_main_yeonhap, context.resources.getString(R.string.yeonhap), "", false))
        list.add(BookmarkItem(R.drawable.ic_main_jtbc, context.resources.getString(R.string.jtbc), "", false))
        list.add(BookmarkItem(R.drawable.ic_main_mbn, context.resources.getString(R.string.mbn), "", false))
        list.add(BookmarkItem(R.drawable.ic_main_youtube, context.resources.getString(R.string.youtube), "", false))
        list.add(BookmarkItem(R.drawable.ic_main_navertv, context.resources.getString(R.string.naver), "", false))

        list.add(BookmarkItem(R.drawable.ic_main_ebs, context.resources.getString(R.string.ebs), "", false))
        list.add(BookmarkItem(R.drawable.ic_main_tbc, context.resources.getString(R.string.tbc), "", false))
        list.add(BookmarkItem(R.drawable.ic_main_tjb, context.resources.getString(R.string.tjb), "", false))
        list.add(BookmarkItem(R.drawable.ic_main_kbc, context.resources.getString(R.string.ikbc), "", false))
        list.add(BookmarkItem(R.drawable.ic_main_ccs, context.resources.getString(R.string.ccstv), "", false))
        list.add(BookmarkItem(R.drawable.ic_main_ubc, context.resources.getString(R.string.ubc), "", false))
        list.add(BookmarkItem(R.drawable.ic_main_jtv, context.resources.getString(R.string.jtv), "", false))
        list.add(BookmarkItem(R.drawable.ic_main_cjb, context.resources.getString(R.string.cjb), "", false))
        list.add(BookmarkItem(R.drawable.ic_main_jibs, context.resources.getString(R.string.jibs), "", false))
        list.add(BookmarkItem(R.drawable.ic_main_kangwon, context.resources.getString(R.string.g1tv), "", false))
//        list.add(BookmarkItem(R.drawable.ic_main_, context.resources.getString(R.string.obs), "", false))
        list.add(BookmarkItem(R.drawable.ic_main_tvchosun, context.resources.getString(R.string.tvchosun), "", false))
        list.add(BookmarkItem(R.drawable.ic_main_channel_a, context.resources.getString(R.string.channela), "", false))
        list.add(BookmarkItem(R.drawable.ic_main_seokyung, context.resources.getString(R.string.iscs), "", false))
        list.add(BookmarkItem(R.drawable.ic_main_mbc_plus, context.resources.getString(R.string.mbcplus), "", false))
        list.add(BookmarkItem(R.drawable.ic_main_gukak, context.resources.getString(R.string.igbf), "", false))
        list.add(BookmarkItem(R.drawable.ic_main_arirang, context.resources.getString(R.string.arirang), "", false))
        list.add(BookmarkItem(R.drawable.ic_main_economy, context.resources.getString(R.string.wowtv), "", false))
        list.add(BookmarkItem(R.drawable.ic_main_ktv, context.resources.getString(R.string.ktv), "", false))

        return list
    }
}
