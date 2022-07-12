/*
 * Create by jhong on 2022. 6. 22.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.ui.main

import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.sorizava.asrplayer.extension.observe
import com.sorizava.asrplayer.ui.base.BaseFragment
import kotlinx.coroutines.flow.flow
import org.mozilla.focus.databinding.FragmentSampleMainOneBinding

class SampleMainOneFragment : BaseFragment<FragmentSampleMainOneBinding>(FragmentSampleMainOneBinding::inflate) {

    private val viewModel: HomeViewModel by viewModels()

    companion object {
        fun newInstance() = SampleMainOneFragment()
    }

    override fun initView() {
    }

    override fun initViewModelObserver() {
        observe(viewModel.homeContents, ::handleTest)


//        counterViewModel.uiState
    }

    private fun handleTest(s: String?) {
        Log.e("TEST", "TEST: $s")
    }
}