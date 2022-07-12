/*
 * Create by jhong on 2022. 7. 7.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */
package com.sorizava.asrplayer.network

import com.google.gson.annotations.Expose
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
class AppApiResponse<T> {
    @JvmField
    @SerializedName("status")
    var status = 0

    @JvmField
    @Expose
    @SerializedName("data")
    var data: T? = null

    @SerializedName("code")
    var code: String? = null
    override fun toString(): String {
        return "LoginResponse{" +
                "status='" + status + '\'' +
                ", data=" + data +
                ", code='" + code + '\'' +
                '}'
    }
}