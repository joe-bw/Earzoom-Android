package kr.co.sorizava.asrplayer;

public class ZerothDefine {

    public static final String API_AUTH_COMPLETE_URL = "http://ailab.sorizava.co.kr:9080/auth/realms/zeroth/protocol/openid-connect/token";
    public static final String API_AUTH_URL = "http://ailab.sorizava.co.kr:9080/";

    public static final String API_APP_KEY = "6bbb73c5-ce84-42e5-a5e7-3fcc9e4d050c";
    public static final String API_APP_SECRET = "ee01365ee13a407593d2";

    // public static final String API_AUTH_APP_ID = "905f1362-8739-40a4-97b1-ce1936a938e9";
    // public static final String API_AUTH_APP_SECRET = "f54d7511966d4dfca08d";

    /**
     * FCM 구독명
     * 개발 구독명 - devEarzoom
     *
     * 운영 구독명 - earzoom
     */
//    public static final String FCM_SUBSCRIBE_NAME =  "devEarzoom";
    public static final String FCM_SUBSCRIBE_NAME =  "earzoom";

    /**
     * 웹서버 URL
     * 개발 서버 - http://211.248.153.107:1969/
     *
     * 실제 운영서버 - https://api.earzoom.kr:8443/
     * 211021
     */
    public static final String BASE_URL = "http://211.248.153.107:1969/";
//    public static final String BASE_URL = "https://api.earzoom.kr:8443/";

    /**
     * 웹서비스 URL 주소
     * 신규등록
     * jhong
     * 211006
     *
     * addInfo -> privacyAgree 로 변경
     * 211021
     **/
    public static final String API_WEB_URL_ADDINFO = BASE_URL + "privacyAgree/";

    /**
     * 웹서비스 URL 주소
     * Notice
     * jhong
     * 211006
     **/
    public static final String API_WEB_URL_NOTICEVIEW = BASE_URL + "noticeView/";

    /**
     * 웹서비스 URL 주소
     * Privacy Policy 개인정보처리
     * jhong
     * 211006
     **/
    public static final String API_WEB_URL_PRIVACY_POLICY = BASE_URL + "privacyPolicy/";


    /** 2021.11.10
     * Banner URL
     * jhong
     */
    public static final String API_WEB_URL_BANNER = "http://datain.co.kr/reply/survey.html?idx=2453";


    /**
     * jhong
     * 211006
     * model >> KOREAN_16K
     * app key 와 app secret 는 사용하지 않습니다.
     * Use project parameter >> OFF
     * ASR Server EndPoint URL >> ws://45.115.152.123:3179/client/ws/speech
     */
    public static final String API_WWS_MODEL = "KOREAN_16K";

    /**
     * jhong
     * 220526
     * TTS 엔진 서버 관리
     * 소리자바 서버 URL
     *
     * */

    /** 소리자바 서버 URL */
    public static final String API_WWS_URL = "ws://ailab.sorizava.co.kr:3179/client/ws/speech";

    /** 신규 배포 URL */
//    public static final String API_WWS_URL = "ws://45.115.152.123:3179/client/ws/speech";

    public static final String API_AUTH_WWS_URL = "ws://ailab.sorizava.co.kr:3179/client/ws/trusted";

    public static final String API_WWS_PARAM = "?single=%s&model=%s&content-type=%s";
    public static final String API_WWS_PARAM_WITH_PROJECT = "?single=%s&project=%s&model=%s&content-type=%s";
    public static final String API_AUTH_WWS_PARAM = "?single=%s&access-token=%s&model=%s&content-type=%s";

    public static final String OPT_16_KHZ = "audio/x-raw,+layout=(string)interleaved,+rate=(int)16000,+format=(string)S16LE,+channels=(int)%d";
    public static final String OPT_44_KHZ = "audio/x-raw,+layout=(string)interleaved,+rate=(int)44100,+format=(string)S16LE,+channels=(int)%d";
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String DATE_UTC_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'";

    public static final String ZEROTH_LANG_KOR = "kor";
    public static final String ZEROTH_LANG_ENG = "eng";

    public static final int ZEROTH_RATE_16 = 16000;
    public static final int ZEROTH_RATE_44 = 44100;

    public static final int ZEROTH_MONO     = 1;
    public static final int ZEROTH_STEREO   = 2;

    public static final int ERROR_GET_TOKEN_FAIL    = 1000;
    public static final int ERROR_SOCKET_FAIL       = 1001;

    public static final int REQUEST_PERMISSIONS_RECORD_AUDIO = 1;

    public enum ZerothStatus {
        IDLE,
        INIT,
        RUNNING,
        STOPED,
        SHUTDOWN
    }
}
