/*
 * Create by jhong on 2022. 6. 22.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.sorizava.asrplayer.ui.base.BaseActivity
import org.mozilla.focus.databinding.ActivitySampleMainBinding

class SampleMainActivity : BaseActivity<ActivitySampleMainBinding>(ActivitySampleMainBinding::inflate

) {

    private val sampleMainOneFragment by lazy { SampleMainOneFragment() }
    private val sampleMainTwoFragment by lazy { SampleMainTwoFragment() }

    override fun initView(savedInstanceState: Bundle?) {

        binding.apply {
            val adapter = SampleAdapter(this@SampleMainActivity)
            val dataList: ArrayList<Fragment> = arrayListOf(sampleMainOneFragment, sampleMainTwoFragment)
            adapter.setItems(dataList)
            viewpagerContent.adapter = adapter
//            dotsIndicator.attachTo(viewpagerContent)
        }
    }
}