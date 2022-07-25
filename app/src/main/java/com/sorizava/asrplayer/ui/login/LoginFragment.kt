/*
 * Create by jhong on 2022. 7. 18.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.ui.login

import android.app.Application
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sorizava.asrplayer.config.LOGIN_TYPE_RELOGIN
import com.sorizava.asrplayer.config.SorizavaLoginManager
import com.sorizava.asrplayer.data.ResultState
import com.sorizava.asrplayer.data.SnsProvider
import com.sorizava.asrplayer.data.model.SnsResultData
import com.sorizava.asrplayer.extension.beGone
import com.sorizava.asrplayer.extension.beVisible
import com.sorizava.asrplayer.extension.observe
import com.sorizava.asrplayer.extension.toast
import com.sorizava.asrplayer.ui.base.BaseFragment
import com.sorizava.asrplayer.ui.privacy.PrivacyPolicyActivity
import org.mozilla.focus.activity.MainActivity
import org.mozilla.focus.databinding.FragmentLoginBinding

class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate),
    AddInfoDialog.AddInfoDialogListener {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var viewModel: LoginViewModel

    override fun initView() {
        val context = activity?.applicationContext

        viewModel = ViewModelProvider(this, LoginViewModelFactory(context as Application))
            .get(LoginViewModel::class.java)

        setUI()

        if (arguments != null) {
            val isReLogin = requireArguments().getBoolean(LOGIN_TYPE_RELOGIN, false)
            if (isReLogin) {
                setReLoginUI()
            }
        }

//        setReLoginUI()

        // fixme 테스트 용도 향후 삭제
        binding.btnTemp.setOnClickListener {
            startActivity(Intent(activity, MainActivity::class.java))
        }
    }

    /** 추가 정보 등록 UI 처리  */
    private fun setReLoginUI() {
        binding.apply {
            layoutAddInfo.beVisible()
            btnLoginNaver.isEnabled = false
            btnLoginKakao.isEnabled = false
            btnLoginFacebook.isEnabled = false
            btnLoginGoogle.isEnabled = false
            btnAddInfo.setOnClickListener {
                checkPrivacy()
            }
        }
    }

    private fun setUI() {
        binding.apply {

            btnLoginNaver.setOnClickListener {
                requestSignIn(SnsProvider.NAVER)
            }

            btnLoginKakao.setOnClickListener {
                requestSignIn(SnsProvider.KAKAO)
            }

            btnLoginFacebook.setOnClickListener {
                requestSignIn(SnsProvider.FACEBOOK)
            }

            btnLoginGoogle.setOnClickListener {
                requestSignIn(SnsProvider.GOOGLE)
            }
        }
    }


    /** 2021.10.31 개인정보 확인  */
    private fun checkPrivacy() {
        if (TextUtils.isEmpty(SorizavaLoginManager.instance?.prefUserBirth)) {
            showNoticeDialog()
        } else {
            /** 회원 가입 여부 확인  */
//            callMemberInfo()

//            viewModel.isLoginMember
        }
    }

    private fun showNoticeDialog() {
        // Create an instance of the dialog fragment and show it
        val dialog: DialogFragment = AddInfoDialog(this)
        dialog.show(activity?.supportFragmentManager!!, "AddInfoDialog")
    }

    private fun requestSignIn(provider: SnsProvider) {
        viewModel.signIn(provider, ::onSuccessLogin, ::onFailedLogin)
    }

    override fun initViewModelObserver() {
        observe(viewModel.signCallback, ::snsCallback)
    }

    private fun onSuccessLogin(result: SnsResultData) {

        hideLoadingBar()
        gotoMainActivity()

        when (result.type) {
            SnsProvider.EMAIL -> {
            }
            SnsProvider.KAKAO -> {
            }
            SnsProvider.NAVER -> {
            }
            SnsProvider.FACEBOOK -> {
            }
            SnsProvider.GOOGLE -> {
            }
            else -> {}
        }
    }

    private fun onFailedLogin(error: SnsResultData) {
        hideLoadingBar()

        when (error.type) {
            SnsProvider.EMAIL -> {

            }
            SnsProvider.KAKAO -> {
                activity?.toast("취소 하였습니다.")
            }
            SnsProvider.NAVER -> {
            }
            SnsProvider.FACEBOOK -> {
            }
            SnsProvider.GOOGLE -> {
            }
            else -> {}
        }
    }

    private fun snsCallback(result: ResultState<Unit>) {
        when(result) {
            is ResultState.Loading -> {
                showLoadingBar()
            }
            else -> {
                hideLoadingBar()
            }
//            is ResultState.Success -> {
//                hideLoadingBar()
//
//                val data = result.data
//
//                // SNS type 별 데이터 저장 후 메인 화면 이동
//                when(data?.type) {
//                    SnsProvider.EMAIL -> {
//
//                    }
//                    SnsProvider.KAKAO -> {
//                    }
//                    SnsProvider.NAVER -> {
//                    }
//                    SnsProvider.FACEBOOK -> {
//                    }
//                    SnsProvider.GOOGLE -> {
//                    }
//                }
//
//
//
//                gotoMainActivity()
//            }
//            is ResultState.Error -> {
//                hideLoadingBar()
//            }
        }
    }


    private fun showLoadingBar() {
        binding.progressLoading.beVisible()
    }

    private fun hideLoadingBar() {
        binding.progressLoading.beGone()
    }

    private fun gotoMainActivity() {
        startActivity(Intent(activity, MainActivity::class.java))
        activity?.finish()
    }

    override fun onDialogPositiveClick(dialog: DialogFragment, birth: String, phone: String) {

        Log.d("TEST", "onDialogPositiveClick: $birth")
        Log.d("TEST", "onDialogPositiveClick: $phone")

        SorizavaLoginManager.instance?.prefUserBirth = birth
        SorizavaLoginManager.instance?.prefUserPhone = phone

        dialog.dismiss()

        /** 추가정보 확인후 회원 가입여부 확인 */
        /** 추가정보 확인후 회원 가입여부 확인  */
//        callMemberInfo()
        viewModel.checkLoginInfo()
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        dialog.dismiss()

    }

    override fun onDialogPrivacyClick(dialog: DialogFragment) {
        startActivity(Intent(activity, PrivacyPolicyActivity::class.java))
    }
}

class LoginViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            LoginViewModel(application) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}