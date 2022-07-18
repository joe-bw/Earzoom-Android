/*
 * Create by jhong on 2022. 7. 18.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.data

data class SnsResult(
    val success: Boolean?,
    val id: String?,
    val password: String?,
    val type: SnsProvider?,
    val error: String?
)
