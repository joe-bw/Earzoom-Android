/*
 * Create by jhong on 2022. 7. 18.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.ui.login

import android.app.Application
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sorizava.asrplayer.data.ResultState
import com.sorizava.asrplayer.data.SnsProvider
import com.sorizava.asrplayer.data.SnsResult
import com.sorizava.asrplayer.extension.beGone
import com.sorizava.asrplayer.extension.beVisible
import com.sorizava.asrplayer.extension.observe
import com.sorizava.asrplayer.ui.base.BaseFragment
import org.mozilla.focus.databinding.FragmentLoginBinding

class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var viewModel: LoginViewModel

    override fun initView() {
        val context = activity?.applicationContext

        viewModel = ViewModelProvider(this, LoginViewModelFactory(context as Application))
            .get(LoginViewModel::class.java)

        setUI()
    }

    private fun setUI() {
        binding.apply {

            btnLoginNaver.setOnClickListener {
                viewModel.signIn(SnsProvider.NAVER)
            }

            btnLoginKakao.setOnClickListener {
                viewModel.signIn(SnsProvider.KAKAO)
            }

            btnLoginFacebook.setOnClickListener {
                viewModel.signIn(SnsProvider.FACEBOOK)
            }

            btnLoginGoogle.setOnClickListener {
                viewModel.signIn(SnsProvider.GOOGLE)
            }
        }
    }

    override fun initViewModelObserver() {
        observe(viewModel.signCallback, ::snsCallback)
    }

    private fun snsCallback(result: ResultState<SnsResult>) {
        when(result) {
            is ResultState.Loading -> {
                showLoadingbar()
            }
            is ResultState.Success -> {
                hideLoadingbar()
            }
            is ResultState.Error -> {
                hideLoadingbar()
            }
        }
    }


    private fun showLoadingbar() {
        binding.progressLoading.beVisible()
    }

    private fun hideLoadingbar() {
        binding.progressLoading.beGone()
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