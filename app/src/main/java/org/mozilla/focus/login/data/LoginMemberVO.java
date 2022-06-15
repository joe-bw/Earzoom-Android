package org.mozilla.focus.login.data;

import com.google.gson.annotations.SerializedName;


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
public class LoginMemberVO {

    @SerializedName("id")
    public String id;

    @SerializedName("pw")
    public String pw;

    @SerializedName("deviceToken")
    public String deviceToken;

    @SerializedName("name")
    public String name;

    @SerializedName("birth")
    public String birth;

    @SerializedName("sex")
    public String sex;

    @SerializedName("addr")
    public String addr;

    @SerializedName("addr1")
    public String addr1;

    @SerializedName("phone")
    public String phone;

    @SerializedName("memAuth")
    public int memAuth;

    @SerializedName("statusLevel")
    public String statusLevel;

    @SerializedName("organ")
    public String organ;

    @SerializedName("kind")
    public String kind;

    @SerializedName("roleName")
    public String roleName;

    @Override
    public String toString() {
        return "LoginResponseDataMember{" +
                "id='" + id + '\'' +
                ", pw='" + pw + '\'' +
                ", deviceToken='" + deviceToken + '\'' +
                ", name='" + name + '\'' +
                ", birth='" + birth + '\'' +
                ", sex='" + sex + '\'' +
                ", addr='" + addr + '\'' +
                ", addr1='" + addr1 + '\'' +
                ", phone='" + phone + '\'' +
                ", memAuth=" + memAuth +
                ", statusLevel='" + statusLevel + '\'' +
                ", organ='" + organ + '\'' +
                ", kind='" + kind + '\'' +
                ", roleName='" + roleName + '\'' +
                '}';
    }
}
