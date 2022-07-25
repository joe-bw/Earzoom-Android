/*
 * Create by jhong on 2022. 7. 13.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.repository

import android.app.Application
import com.sorizava.asrplayer.data.SnsProvider
import com.sorizava.asrplayer.data.model.SnsResultData
import com.sorizava.asrplayer.dataSource.SnsDataSource

class SnsLoginRepository(
    private val context: Application
) : SnsLoginRepositorySource {
    override fun signIn(
        provider: SnsProvider,
        onSuccess: (SnsResultData) -> Unit,
        onFailed: (SnsResultData) -> Unit
    ) {
        when (provider) {
            SnsProvider.EMAIL -> {}
            SnsProvider.KAKAO -> {
                SnsDataSource.callKakaoLogin(context, onSuccess, onFailed)
            }
            SnsProvider.NAVER -> {
                SnsDataSource.callNaverLogin(context, onSuccess, onFailed)
            }
            SnsProvider.FACEBOOK -> {
                SnsDataSource.callFacebookLogin(context, onSuccess, onFailed)
            }
            SnsProvider.GOOGLE -> {
//                SnsDataSource.callGoogleLogin(context)
            }
        }
    }
}