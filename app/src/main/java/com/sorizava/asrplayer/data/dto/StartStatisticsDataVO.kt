/*
 * Create by jhong on 2022. 7. 7.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */
package com.sorizava.asrplayer.data.dto

import com.google.gson.annotations.SerializedName
import com.sorizava.asrplayer.data.vo.DataResultVO

/**
 *
 * {
 * "status": 200,
 * "data": {
 * "result": {
 * "statisticsSeq": "11",
 * "id": "sorizava",
 * "connDate": "2021-08-03",
 * "stTime": "2021-08-03 15:44:00.0",
 * "endTime": null,
 * "useContent": "http://www.useContent.com"
 * }
 * },
 * "code": "정상"
 * }
 *
 * {
 * "status": "200",
 * "data": {
 * "result": {
 * "statisticsSeq": "43",
 * "id": "1826472143",
 * "connDate": "2021-10-12",
 * "stTime": "2021-10-12 10:56:02",
 * "endTime": "null",
 * "useContent": "http://www.kbs.co.kr/"
 * },
 * "code": "정상"
 * }
 * }
 *
 *
 */
data class StartStatisticsDataVO(
    @SerializedName("result")
    var result: DataResultVO? = null
)