package kr.co.sorizava.asrplayerKt.network

import kr.co.sorizava.asrplayerKt.network.ErrorModel
import kr.co.sorizava.asrplayerKt.network.OAuthToken

interface OnGetTokenListener {
    fun onGetToken(token: String?)
    fun onGetToken(oAuthToken: OAuthToken?)
    fun onFailed(error: ErrorModel?)
}