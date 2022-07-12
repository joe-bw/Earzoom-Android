/*
 * Create by jhong on 2022. 7. 7.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */
package com.sorizava.asrplayer.data.dto

import com.google.gson.annotations.SerializedName

/**
 * {
 * "status": "success",
 * "data": {
 * "member": {
 * "id": "kang",
 * "pw": "$2a$10$3/DC8Wx8MWakM2yLSqX61e4dr2UwdYy2OmaZX2J6WY29E8h3gsvsa",
 * "deviceToken": "device_token_test",
 * "name": "sori",
 * "birth": "20210618",
 * "sex": "여성",
 * "addr": "서울시",
 * "addr1": "역삼동",
 * "phone": "010-1111-1111",
 * "memAuth": 1,
 * "statusLevel": null,
 * "organ": "",
 * "kind": null,
 * "roleName": null
 * }
 * },
 * "code": "정상"
 * }
 */
data class LoginMemberVO (
    @JvmField
    @SerializedName("id")
    var id: String? = null,

    @SerializedName("pw")
    var pw: String? = null,

    @SerializedName("deviceToken")
    var deviceToken: String? = null,

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("birth")
    var birth: String? = null,

    @SerializedName("sex")
    var sex: String? = null,

    @SerializedName("addr")
    var addr: String? = null,

    @SerializedName("addr1")
    var addr1: String? = null,

    @SerializedName("phone")
    var phone: String? = null,

    @SerializedName("memAuth")
    var memAuth: Int = 0,

    @SerializedName("statusLevel")
    var statusLevel: String? = null,

    @SerializedName("organ")
    var organ: String? = null,

    @SerializedName("kind")
    var kind: String? = null,

    @SerializedName("roleName")
    var roleName: String? = null,
)