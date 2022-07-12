/*
 * Create by jhong on 2022. 7. 11.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.ui.intro

import android.annotation.SuppressLint
import android.app.Application
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.Task
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.sorizava.asrplayer.config.LoginManager
import com.sorizava.asrplayer.data.IntroCode
import com.sorizava.asrplayer.data.vo.LoginDataVO
import com.sorizava.asrplayer.data.vo.LoginNewRequest
import com.sorizava.asrplayer.extension.observe
import com.sorizava.asrplayer.network.AppApiClient
import com.sorizava.asrplayer.network.AppApiResponse
import com.sorizava.asrplayer.ui.base.BaseFragment
import org.mozilla.focus.R
import org.mozilla.focus.databinding.FragmentIntroBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 인트로 화면이 나타나는 가운데,
 * 버전 확인
 * Firebase 설정
 *
 */
class IntroFragment : BaseFragment<FragmentIntroBinding>(FragmentIntroBinding::inflate) {

    companion object {
        fun newInstance() = IntroFragment()
    }

    private lateinit var viewModel: IntroViewModel

    override fun initView() {
        val context = activity?.applicationContext

        viewModel = ViewModelProvider(this, IntroViewModelFactory(context as Application))
            .get(IntroViewModel::class.java)
    }

    override fun initViewModelObserver() {
        observe(viewModel.appVersion, ::handleAppVersion)
        observe(viewModel.validAppVersion, ::handleValidSettings)
        observe(viewModel.isValidLogin, ::handleValidLogin)
    }

    private fun handleValidLogin(isValid: Boolean) {
        when(isValid) {
            true -> {

            }
            false -> {

            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleAppVersion(version: String) {
        binding.txtVersion.text = String.format(getString(R.string.setting_app_version_prefix)) + " $version"
    }

    private fun handleValidSettings(isValid: IntroCode) {
        when(isValid) {
            IntroCode.LOADING -> {
                // TODO -> nothing happen
            }

            IntroCode.NEED_APP_UPDATE -> {
                showAppUpdateDialog()
            }

            IntroCode.GOTO_MAIN -> {
                viewModel.checkLoginInfo()
            }
        }
    }

    private fun showAppUpdateDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.txt_app_update))
            .setMessage(getString(R.string.txt_app_update_need))
            .setPositiveButton(getString(R.string.txt_app_update)) { _: DialogInterface?, _: Int ->
                val marketLaunch = Intent(Intent.ACTION_VIEW)
                marketLaunch.data =
                    Uri.parse("https://play.google.com/store/apps/details?id=${requireContext().packageName}")
                startActivity(marketLaunch)
                activity?.finish()
            }
            .setNegativeButton(getString(R.string.action_cancel)) { _: DialogInterface?, _: Int -> activity?.finish() }
        val alertDialog = builder.create()
        alertDialog.show()
    }
}

class IntroViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(IntroViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            IntroViewModel(application) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}