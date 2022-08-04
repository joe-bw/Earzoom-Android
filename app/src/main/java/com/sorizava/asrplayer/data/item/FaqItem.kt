/*
 * Create by jhong on 2022. 7. 26.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.data.item

/**
 * FAQ 리스트
 *
 * response varue
 *
 * {
 *  "faqSeq": 7? = null,
 *  "faqTitle": "자주묻는질문"? = null,
 *  "faqContent": "자주묻는질문에 대한 답입니다."? = null,
 *  "faqWriter": "admin"? = null,
 *  "regDt": "2022-07-12 17:32:03"? = null,
 *  "modDt": null? = null,
 *  "linkUrl": "/faqWebView?faqSeq=7"
 *  }
 */
data class FaqItem(
    var faqSeq: Int? = null,
    var faqTitle: String? = null,
    var faqContent: String? = null,
    var faqWriter: String? = null,
    var regDt: String? = null,
    var modDt: String? = null,
    var linkUrl: String? = null,
)
