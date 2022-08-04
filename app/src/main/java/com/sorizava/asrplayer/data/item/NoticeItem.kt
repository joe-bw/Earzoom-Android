/*
 * Create by jhong on 2022. 7. 26.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.data.item

import com.google.gson.annotations.SerializedName

/**
 * 공지사항 리스트
 *
 * response varue
 *
 * "noticeSeq": 88? = null,
 * "noticeTitle": "공지사항"? = null,d
 * "noticeContent": "<p>공지사항</p><p><br></p><p>공지사항 수정입니다.</p>"? = null,
 * "noticeWriter": "admin"? = null,
 * "regDt": "2022-07-12 11:43:43"? = null,
 * "modDt": "2022-07-12 13:48:16.0"? = null,
 * "linkUrl": "/noticeWebView?noticeSeq=88"
 */
data class NoticeItem(
    var noticeSeq: Int? = null,
    var noticeTitle: String? = null,
    var noticeContent: String? = null,
    var noticeWriter: String? = null,
    var regDt: String? = null,
    var modDt: String? = null,
    var linkUrl: String? = null,
)
