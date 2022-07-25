/*
 * Create by jhong on 2022. 7. 18.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.data

import com.sorizava.asrplayer.data.model.SnsResultData

data class SnsResult(
    val success: Boolean?,
    val token: String?,
    val id: String?,
    val password: String?,
    val error: String?,

    override var type: SnsProvider?,

): SnsResultData
