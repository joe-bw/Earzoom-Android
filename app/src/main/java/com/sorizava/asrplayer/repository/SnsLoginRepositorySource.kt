/*
 * Create by jhong on 2022. 7. 13.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.repository

import com.sorizava.asrplayer.data.SnsProvider
import com.sorizava.asrplayer.data.model.SnsResultData

interface SnsLoginRepositorySource {
    fun signIn(
        provider: SnsProvider,
        onSuccess: (SnsResultData) -> Unit,
        onFailed: (SnsResultData) -> Unit
    )
}