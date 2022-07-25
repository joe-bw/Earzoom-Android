/*
 * Create by jhong on 2022. 7. 19.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.data.model

import com.sorizava.asrplayer.data.SnsProvider

class GoogleData(
    val token: String?,
    val id: String?,
    val password: String?,
    override var type: SnsProvider?
) : SnsResultData