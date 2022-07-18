/*
 * Create by jhong on 2022. 7. 12.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.data

enum class IntroState(var code: Int) {
    LOADING(0),
    NEED_APP_UPDATE(1),
    CHECK_LOGIN(2),
    GOTO_LOGIN(3),
    GOTO_MAIN(4),
}