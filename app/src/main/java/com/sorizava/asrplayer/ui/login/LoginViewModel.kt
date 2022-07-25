/*
 * Create by jhong on 2022. 7. 18.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.ui.login

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.core.util.Consumer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facebook.CallbackManager
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.sorizava.asrplayer.config.SorizavaLoginManager
import com.sorizava.asrplayer.data.IntroState
import com.sorizava.asrplayer.data.ResultState
import com.sorizava.asrplayer.data.SnsProvider
import com.sorizava.asrplayer.data.model.SnsResultData
import com.sorizava.asrplayer.data.vo.LoginNewRequest
import com.sorizava.asrplayer.repository.LoginRepository
import com.sorizava.asrplayer.repository.SnsLoginRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.mozilla.focus.ext.application

class LoginViewModel(private val context: Application) : ViewModel() {

    private var signInLauncher: ActivityResultLauncher<Intent>? = null

    private val signInLauncherCallback: Consumer<SnsResultData>? = null

    var callbackManager: CallbackManager? = null

    private val loginStatePrivate = MutableLiveData<IntroState>()
    val loginState: LiveData<IntroState> = loginStatePrivate

    private val signCallbackPrivate = MutableLiveData<ResultState<Unit>>()
    val signCallback: LiveData<ResultState<Unit>> = signCallbackPrivate

    private var repository: SnsLoginRepository = SnsLoginRepository(context)

    fun initLauncher(activity: ComponentActivity) {
        signInLauncher = activity.registerForActivityResult(
            FirebaseAuthUIActivityResultContract()
        ) { result: FirebaseAuthUIAuthenticationResult ->
            val response = result.idpResponse
            if (result.resultCode == Activity.RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser
                if (response == null || user == null) {
                    Log.e("Google API", "response null")
                } else {
//                    val joinVO: SignUpVO = initJoinVO(
//                        SnsProvider.GOOGLE,
//                        response.idpToken,
//                        user.uid,
//                        user.email
//                    )
//                    signInLauncherCallback?.accept(joinVO)
                }
            } else {
                Log.e(
                    "Google API",
                    "Google SignIn Failed >>> " + result.resultCode
                )
            }
        }
    }

    fun signIn(
        provider: SnsProvider,
        onSuccess: (SnsResultData) -> Unit,
        onFailed: (SnsResultData) -> Unit
    ) {
        viewModelScope.launch {
            signCallbackPrivate.value = ResultState.Loading()
            repository.signIn(provider, onSuccess, onFailed)
        }
    }

    fun checkLoginInfo() {
        viewModelScope.launch {

            val birth = SorizavaLoginManager.instance?.prefUserBirth
            val phone = SorizavaLoginManager.instance?.prefUserPhone

            if (SorizavaLoginManager.instance?.userSNSType == SorizavaLoginManager.SNS_TYPE_NONE) {
                loginStatePrivate.value = IntroState.GOTO_LOGIN
            } else if (TextUtils.isEmpty(birth)) {
                loginStatePrivate.value = IntroState.GOTO_LOGIN
            } else {
                if (birth != null && phone != null){
                    val request = LoginNewRequest(birth, phone)
                    val repository = LoginRepository(context.application, request)
                    repository.requestMemberInfo().collect {
                        when (it) {
                            ResultState.Success(data = it) -> {
                                loginStatePrivate.value = IntroState.GOTO_MAIN
                            }
                            else -> {
                                loginStatePrivate.value = IntroState.GOTO_LOGIN
                            }
                        }
                    }
                }
            }
        }
    }
}


