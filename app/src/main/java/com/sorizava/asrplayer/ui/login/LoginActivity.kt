/*
 * Create by jhong on 2022. 7. 18.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.ui.login

import android.os.Bundle
import com.sorizava.asrplayer.ui.base.BaseActivity
import org.mozilla.focus.R
import org.mozilla.focus.databinding.ActivityLogin2Binding

class LoginActivity : BaseActivity<ActivityLogin2Binding>(ActivityLogin2Binding::inflate) {

    override fun initView(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, LoginFragment.newInstance())
                .commitNow()
        }
    }

}