package org.mozilla.focus.login.data;

/**
 *
 * {
 *     "status": "success",
 *     "data": {
 *         "member": {
 *             "id": "kang",
 *             "pw": "$2a$10$3/DC8Wx8MWakM2yLSqX61e4dr2UwdYy2OmaZX2J6WY29E8h3gsvsa",
 *             "deviceToken": "device_token_test",
 *             "name": "sori",
 *             "birth": "20210618",
 *             "sex": "여성",
 *             "addr": "서울시",
 *             "addr1": "역삼동",
 *             "phone": "010-1111-1111",
 *             "memAuth": 1,
 *             "statusLevel": null,
 *             "organ": "",
 *             "kind": null,
 *             "roleName": null
 *         }
 *     },
 *     "code": "정상"
 * }
 */
import com.google.gson.annotations.SerializedName;

public class LoginDataVO {

    @SerializedName("member")
    public LoginMemberVO member;

    @Override
    public String toString() {
        return "LoginResponseData{" +
                "member=" + member +
                '}';
    }
}
