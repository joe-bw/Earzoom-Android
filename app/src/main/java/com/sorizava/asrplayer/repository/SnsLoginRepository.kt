/*
 * Create by jhong on 2022. 7. 13.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.repository

import android.app.Application
import com.sorizava.asrplayer.data.ResultState
import com.sorizava.asrplayer.data.SnsProvider
import com.sorizava.asrplayer.data.SnsResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class SnsLoginRepository(
    private val context: Application
) : SnsLoginRepositorySource {
    override fun signIn(provider: SnsProvider): Flow<ResultState<SnsResult>> {
        return flow {

//            when (provider) {
//                SnsProvider.EMAIL -> {}
//                SnsProvider.KAKAO -> {
//                    SnsModel.callKaKaoLogin(context)
//                }
//                SnsProvider.NAVER -> {
//                    context.applicationContext.getString(R.string.naver_client_id)
//
//                    NaverIdLoginSDK.apply {
//                        initialize(context, )
//                    }
//                }
//                SnsProvider.FACEBOOK -> {}
//                SnsProvider.GOOGLE -> {}
//                else -> {}
//            }
//


//            val success: Boolean?,
//            val id: String?,
//            val password: String?,
//            val type: SnsProvider?,
//            val error: String?

            val result = SnsResult(true, "id", "pas", SnsProvider.GOOGLE, null)

            emit(ResultState.Success(result))
        }.flowOn(Dispatchers.IO)
    }

//    private val setData(provider: SnsProvider, id: String): SnsResult {
//        return SnsResul(provider)
//    }

}