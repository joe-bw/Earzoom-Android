/*
 * Create by jhong on 2022. 7. 7.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */
package com.sorizava.asrplayer.network

import com.sorizava.asrplayer.data.vo.EndStatisticsRequest
import retrofit2.http.POST
import com.sorizava.asrplayer.data.vo.LoginNewRequest
import com.sorizava.asrplayer.data.vo.LoginDataVO
import com.sorizava.asrplayer.data.vo.LogoutRequest
import com.sorizava.asrplayer.data.vo.StartStatisticsRequest
import com.sorizava.asrplayer.data.dto.StartStatisticsDataVO
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body

/**
 * "id":"kang",
 * "pw":"1234",
 * "deviceToken":"device_token_test"
 */
interface AppApiService {
    @POST("api/member/getMember")
    fun requestMemberInfo(
        @Body param: LoginNewRequest
    ): Response<AppApiResponse<LoginDataVO>>

    @POST("api/member/deleteMember")
    fun requestLogout(
        @Body param: LogoutRequest
    ): Call<AppApiResponse<LoginDataVO>>

    // 더이상 쓰지 않음
    @POST("api/member/loginMember")
    fun requestLogin(
        @Body param: LoginNewRequest
    ): Response<AppApiResponse<LoginDataVO>>

    @POST("api/stStatistics")
    fun requestStartStatistics(
        @Body param: StartStatisticsRequest
    ): Call<AppApiResponse<StartStatisticsDataVO>>

    @POST("api/endStatistics")
    fun requestEndStatistics(
        @Body param: EndStatisticsRequest
    ): Call<AppApiResponse<Int>>
}