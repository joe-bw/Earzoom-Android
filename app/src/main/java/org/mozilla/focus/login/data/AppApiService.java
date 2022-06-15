package org.mozilla.focus.login.data;

import org.mozilla.focus.login.ui.statistics.EndStatisticsRequest;
import org.mozilla.focus.login.ui.statistics.StartStatisticsDataVO;
import org.mozilla.focus.login.ui.statistics.StartStatisticsRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * "id":"kang",
 *     "pw":"1234",
 *     "deviceToken":"device_token_test"
 */
public interface AppApiService {

    @POST("api/member/getMember")
    Call<AppApiResponse<LoginDataVO>> requestMemberInfo(
            @Body LoginNewRequest param
    );

    @POST("api/member/deleteMember")
    Call<AppApiResponse<LoginDataVO>> requestLogout(
            @Body LogoutRequest param
    );

    // 더이상 쓰지 않음
    @POST("api/member/loginMember")
    Call<AppApiResponse<LoginDataVO>> requestLogin(
            @Body LoginNewRequest param
    );

    @POST("api/stStatistics")
    Call<AppApiResponse<StartStatisticsDataVO>> requestStartStatistics(
            @Body StartStatisticsRequest param
    );

    @POST("api/endStatistics")
    Call<AppApiResponse<Integer>> requestEndStatistics(
            @Body EndStatisticsRequest param
    );
}
