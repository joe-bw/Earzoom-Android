/*
 * Create by jhong on 2022. 7. 26.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.data.item

/**
 * Event 리스트
 *
 * response varue
 *
 *  {
 *  "eventSeq": 6? = null,
 *  "eventTitle": " [단독] 금감원 직원들? = null, 주식 투자 규정 위반 '무더기' 징계"? = null,
 *  "eventContent": "<p class=\"editor-p read\" data-break-type=\"text\" style=\"box-sizing: border-box; position: relative; font-size: 18px; line-height: 34px; font-family: &quot;Noto Sans KR&quot;? = null, &quot;Malgun Gothic&quot;? = null, &quot;맑은 고딕&quot;? = null, Helvetica? = null, Arial? = null, sans-serif; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: -0.35px;\">금융상품 투자 규정을 위반한 사실이 적발된 금융감독원 직원들이 무더기로 징계를 받았다.</p><p class=\"editor-p read\" data-break-type=\"text\" style=\"margin-top: 21px; box-sizing: border-box; position: relative; font-size: 18px; line-height: 34px; font-family: &quot;Noto Sans KR&quot;? = null, &quot;Malgun Gothic&quot;? = null, &quot;맑은 고딕&quot;? = null, Helvetica? = null, Arial? = null, sans-serif; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: -0.35px;\">12일 금융권에 따르면 금융위원회는 전날 홈페이지에 공개한 제9차 금융위원회 정례회의 자료를 통해 ‘금융감독원 소속 직원에 대한 자체조사 결과 조치안’을 의결했다. 9차 정례회의는 5월 11일 개최됐다.</p><p class=\"editor-p read\" data-break-type=\"text\" style=\"margin-top: 21px; box-sizing: border-box; position: relative; font-size: 18px; line-height: 34px; font-family: &quot;Noto Sans KR&quot;? = null, &quot;Malgun Gothic&quot;? = null, &quot;맑은 고딕&quot;? = null, Helvetica? = null, Arial? = null, sans-serif; font-style: normal; font-variant-ligatures: normal; font-variant-caps: normal; font-weight: 400; letter-spacing: -0.35px;\">징계가 확정된 금감원 직원은 총 9명이다. 이들은 모두 개인 주식 투자 거래 시 금감원 직원이 지켜야 할 절차를 준수하지 않은 것으로 드러났다. 자본시장법은 불공정행위ㆍ이해상충을 방지하기 위해 금융회사 임직원의 투자를 엄격히 제한하고 있다. 특히&nbsp;<span class=\"read\" style=\"font-weight: bolder;\">금융회사 임직원이 투자할 경우 △자기 명의로 하나의 계좌를 통해&nbsp;</span><span class=\"read\" style=\"font-weight: bolder;\">매매하고 △분기마다 매매 명세를 회사에 통지하도록</span>&nbsp;하고 있는데? = null, 금감원 임직원에도 적용된다.</p>"? = null,
 *  "eventWriter": "admin"? = null,
 *  "regDt": "2022-07-13 16:44:29"? = null,
 *  "modDt": null? = null,
 *  "linkUrl": "/eventWebView?eventSeq=6"
 *  }
 */
data class EventItem(
    var eventSeq: Int? = null,
    var eventTitle: String? = null,
    var eventContent: String? = null,
    var eventWriter: String? = null,
    var regDt: String? = null,
    var modDt: String? = null,
    var linkUrl: String? = null,
)
