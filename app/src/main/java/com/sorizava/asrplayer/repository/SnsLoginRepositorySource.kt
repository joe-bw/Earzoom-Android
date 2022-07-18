/*
 * Create by jhong on 2022. 7. 13.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.repository

import com.sorizava.asrplayer.data.ResultState
import com.sorizava.asrplayer.data.SnsProvider
import com.sorizava.asrplayer.data.SnsResult
import com.sorizava.asrplayer.data.vo.LoginDataVO
import com.sorizava.asrplayer.data.vo.LoginNewRequest
import com.sorizava.asrplayer.network.AppApiResponse
import kotlinx.coroutines.flow.Flow

interface SnsLoginRepositorySource {
    fun signIn(provider: SnsProvider): Flow<ResultState<SnsResult>>
}