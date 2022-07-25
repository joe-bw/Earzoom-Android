/*
 * Create by jhong on 2022. 7. 18.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.dataSource

import android.app.Application
import android.util.Log
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.model.User
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthBehavior
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.sorizava.asrplayer.data.model.SnsResultData
import com.sorizava.asrplayer.data.model.KakaoData
import com.sorizava.asrplayer.data.model.NaverData
import org.mozilla.focus.R
import org.mozilla.focus.extension.toActivity


object SnsDataSource {

    private val TAG = SnsDataSource.javaClass.simpleName

//    fun callKaKaoLogin(context: Application): KakaoData {
//        return KakaoData()
//    }

    fun callKakaoLogin(
        context: Application,
        onSuccess: (SnsResultData) -> Unit,
        onFailed: (SnsResultData) -> Unit
    ) {
        // 기기에 카카오톡 설치되있는 경우
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance
                .loginWithKakaoTalk(context) { token: OAuthToken?, error: Throwable? ->
                    kakaoLoginCallback(
                        token,
                        error,
                        onSuccess,
                        onFailed
                    )
                }
        } else {
            UserApiClient.instance
                .loginWithKakaoAccount(context) { token: OAuthToken?, error: Throwable? ->
                    kakaoLoginCallback(
                        token,
                        error,
                        onSuccess,
                        onFailed
                    )
                }
        }
    }

    fun callNaverLogin(
        context: Application,
        onSuccess: (SnsResultData) -> Unit,
        onFailed: (SnsResultData) -> Unit
    ){
        NaverIdLoginSDK.behavior = NidOAuthBehavior.DEFAULT
        NaverIdLoginSDK.authenticate(context, object : OAuthLoginCallback {
            override fun onSuccess() {

                val accessToken = NaverIdLoginSDK.getAccessToken()
                val refreshToken = NaverIdLoginSDK.getRefreshToken()
                val expireAt = NaverIdLoginSDK.getExpiresAt().toString()
                val tokenType = NaverIdLoginSDK.getTokenType()
                val state = NaverIdLoginSDK.getState().toString()

                Log.e(TAG, "accessToken: $accessToken")
                Log.e(TAG, "refreshToken: $refreshToken")
                Log.e(TAG, "expireAt: $expireAt")
                Log.e(TAG, "tokenType: $tokenType")
                Log.e(TAG, "state: $state")

                val result = NaverData(
                    token = accessToken
                )

                onSuccess.invoke(result)
            }

            override fun onFailure(httpStatus: Int, message: String) {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()

                Log.e(TAG, "errorCode: $errorCode")
                Log.e(TAG, "errorDescription: $errorDescription")

                val result = NaverData(
                    errorCode = errorCode,
                    errorDescription = errorDescription
                )

                onFailed.invoke(result)
            }

            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }
        })
    }

    private fun kakaoLoginCallback(
        token: OAuthToken?,
        error: Throwable?,
        onSuccess: (SnsResultData) -> Unit,
        onFailed: (SnsResultData) -> Unit
    ) {
        if (error != null) {
            Log.e(TAG, "Kakao login failed", error)

            val result = KakaoData(
                errorMessage = error.message
            )

            onFailed.invoke(result)

        } else if (token != null) {
            // 유저정보 획득
            UserApiClient.instance.me { user: User?, e: Throwable? ->
                if (e != null) {
                    e.printStackTrace()

                    val result = KakaoData(
                        errorMessage = e.message
                    )

                    onFailed.invoke(result)

                } else if (user != null) {
                    val account = user.kakaoAccount
                    var email: String? = null
                    if (account != null) {
                        email = account.email
                    }

                    val result = KakaoData(
                        token = token.accessToken,
                        id = user.id.toString(),
                        email = email
                    )

                    onSuccess.invoke(result)
                }
            }
        }
    }

    fun callFacebookLogin(
        context: Application,
        onSuccess: (SnsResultData) -> Unit,
        onFailed: (SnsResultData) -> Unit
    ) {
        val callbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    val token = loginResult.accessToken.token
                    val credential = FacebookAuthProvider.getCredential(token)
                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener(context.toActivity(),
                            OnCompleteListener { task: Task<AuthResult?> ->
                                if (task.isSuccessful) {
                                    val user = FirebaseAuth.getInstance().currentUser
                                    if (user != null) {
//                                        callback.accept(
//                                            initJoinVO(
//                                                SnsProvider.FACEBOOK,
//                                                token,
//                                                user.uid,
//                                                user.email
//                                            )
//                                        )
                                    }
                                } else {
//                                    Log.w(
//                                        "Facebook",
//                                        "signInWithCredential:failure",
//                                        task.exception
//                                    )
//                                    Toast.makeText(
//                                        activity,
//                                        "Authentication failed.",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
                                }
                            })
                }

                override fun onCancel() {
                    Log.i("Facebook", "cancel")
                }

                override fun onError(error: FacebookException) {
                    Log.i("Facebook", "error >> $error")
                }
            })
        LoginManager.getInstance()
            .logInWithReadPermissions(context.toActivity(), listOf("email", "public_profile"))
    }

}