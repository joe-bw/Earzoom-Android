package kr.co.sorizava.asrplayer;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.PreferenceManager;

import org.mozilla.focus.R;

/**
 * Global App Config utility
 */
public class AppConfig {

    // 자막 위치
    public static final int SUBTITLE_POSITION_TOP = 0;
    public static final int SUBTITLE_POSITION_BOTTOM = 1;

    // 자막 폰트 크기
    public static final int SUBTITLE_FONT_SIZE_VERY_SMALL = 0;
    public static final int SUBTITLE_FONT_SIZE_SMALL = 1;
    public static final int SUBTITLE_FONT_SIZE_MEDIUM = 2;
    public static final int SUBTITLE_FONT_SIZE_LARGE = 3;
    public static final int SUBTITLE_FONT_SIZE_VERY_LARGE = 4;

    // 자막 라인 수
    public static final int SUBTITLE_LINE_2 = 2;
    public static final int SUBTITLE_LINE_3 = 3;
    public static final int SUBTITLE_LINE_4 = 4;

    private static AppConfig instance = null;

    private Context mContext;
    
    private SharedPreferences mSharedPref;
    
    private AppConfig(Context context) {
        mContext = context;
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    /**
     * SzApp 에서 init
     *
     * @param context Context
     */
    public static void init(Context context) {
        if (instance == null) {
            instance = new AppConfig(context);
        }
    }

    /**
     * Config instance
     *
     * @return  instance of this class.
     */
    public static AppConfig getInstance() {
        return instance;
    }

    // get subtitle on/off
    public boolean getPrefSubtitleOnOff() {
        return mSharedPref.getBoolean("SP_KEY_SZ_SUBTITLE_ON", true);
    }

    // set subtitle on/off
    public void setPrefSubtitleOnOff(boolean z) {
        SharedPreferences.Editor edit = mSharedPref.edit();
        edit.putBoolean("SP_KEY_SZ_SUBTITLE_ON", z);
        edit.apply();
    }

    // get subtitle sync status
    public boolean getPrefSubtitleSync() {
        return mSharedPref.getBoolean("SP_KEY_SZ_SUBTITLE_SYNC", true);
    }

    // set subtitle sync status
    // param boolean: true:noraml sync ,  flase:negative sync (자막 먼저 보기)
    public void setPrefSubtitleSync(boolean z) {
        SharedPreferences.Editor edit = mSharedPref.edit();
        edit.putBoolean("SP_KEY_SZ_SUBTITLE_SYNC", z);
        edit.apply();
    }

    // get subtitle position
    public int getPrefSubtitlePosition() {
        return mSharedPref.getInt("SP_KEY_SZ_SUBTITLE_POSITION",
                SUBTITLE_POSITION_BOTTOM);  // 화면 아래
    }

    // set subtitle position
    public void setPrefSubtitlePosition(int i) {
        SharedPreferences.Editor edit = mSharedPref.edit();
        edit.putInt("SP_KEY_SZ_SUBTITLE_POSITION", i);
        edit.apply();
    }

    // get subtitle max line
    public int getPrefSubtitleLine() {
        return mSharedPref.getInt("SP_KEY_SZ_SUBTITLE_LINE",
            SUBTITLE_LINE_3);  // 3줄
    }

    // set subtitle max line
    public void setPrefSubtitleLine(int i) {
        SharedPreferences.Editor edit = mSharedPref.edit();
        edit.putInt("SP_KEY_SZ_SUBTITLE_LINE", i);
        edit.apply();
    }

    // get subtitle font index
    public int getPrefSubtitleFont() {
        return mSharedPref.getInt("SP_KEY_SZ_SUBTITLE_FONT", 0); // system default: 0
    }

    // set subtitle font index
    public void setPrefSubtitleFont(int i) {
        SharedPreferences.Editor edit = mSharedPref.edit();
        edit.putInt("SP_KEY_SZ_SUBTITLE_FONT", i);
        edit.apply();
    }

    // get subtitle font size
    public int getPrefSubtitleFontSize() {
        return mSharedPref.getInt("SP_KEY_SZ_SUBTITLE_FONT_SIZE",
                SUBTITLE_FONT_SIZE_MEDIUM); // 중간 크기
    }

    // set subtitle font size
    public void setPrefSubtitleFontSize(int i) {
        SharedPreferences.Editor edit = mSharedPref.edit();
        edit.putInt("SP_KEY_SZ_SUBTITLE_FONT_SIZE", i);
        edit.apply();
    }

    // get subtitle foreground color
    public int getPrefSubtitleForegroundColor() {
        return mSharedPref.getInt("SP_KEY_SZ_SUBTITLE_FOREGROUND_COLOR", Color.WHITE);
    }

    // set subtitle foreground color
    public void setPrefSubtitleForegroundColor(int color) {
        SharedPreferences.Editor edit = mSharedPref.edit();
        edit.putInt("SP_KEY_SZ_SUBTITLE_FOREGROUND_COLOR", color);
        edit.apply();
    }

    // get subtitle background transparency
    public int getPrefSubtitleTransparency() {
        return mSharedPref.getInt("SP_KEY_SZ_SUBTITLE_TRANSPARENCY", 50);    // 0.5f (0-100:0~1)
    }

    // set subtitle background transparency
    public void setPrefSubtitleTransparency(int i) {
        SharedPreferences.Editor edit = mSharedPref.edit();
        edit.putInt("SP_KEY_SZ_SUBTITLE_TRANSPARENCY", i);
        edit.apply();
    }

    // get asr auth connection
    public boolean getPrefAsrAuthConnect() {
        return mSharedPref.getBoolean("SP_KEY_ASR_AUTH_CONNECT", false);
    }

    // set asr auth connection
    public void setPrefAsrAuthConnect(boolean z) {
        SharedPreferences.Editor edit = mSharedPref.edit();
        edit.putBoolean("SP_KEY_ASR_AUTH_CONNECT", z);
        edit.apply();
    }

    // get asr connection use param project
    public boolean getPrefAsrUseParamProject() {
        return mSharedPref.getBoolean("SP_KEY_ASR_USE_PARAM_PROJECT", true);
    }

    // set asr connection use param project
    public void setPrefAsrUseParamProject(boolean z) {
        SharedPreferences.Editor edit = mSharedPref.edit();
        edit.putBoolean("SP_KEY_ASR_USE_PARAM_PROJECT", z);
        edit.apply();
    }

    // get ASR server model
    public String getPrefAsrModel() {
        return mSharedPref.getString("SP_KEY_ASR_SERVER_MODEL", ZerothDefine.API_WWS_MODEL);
    }

    // set ASR server model
    public void setPrefAsrModel(String name) {
        SharedPreferences.Editor edit = mSharedPref.edit();
        edit.putString("SP_KEY_ASR_SERVER_MODEL", name);
        edit.apply();
    }

    // get app key
    public String getPrefAppKey() {
        return mSharedPref.getString("SP_KEY_ASR_APP_KEY", ZerothDefine.API_APP_KEY);
    }

    // set app key
    public void setPrefAppKey(String id) {
        SharedPreferences.Editor edit = mSharedPref.edit();
        edit.putString("SP_KEY_ASR_APP_KEY", id);
        edit.apply();
    }

    // get app secret
    public String getPrefAppSecret() {
        return mSharedPref.getString("SP_KEY_ASR_APP_SECRET", ZerothDefine.API_APP_SECRET);
    }

    // set app secret
    public void setPrefAppSecret(String key) {
        SharedPreferences.Editor edit = mSharedPref.edit();
        edit.putString("SP_KEY_ASR_APP_SECRET", key);
        edit.apply();
    }

    // get ASR server url
    public String getPrefAsrUrl() {
        return mSharedPref.getString("SP_KEY_ASR_URL", ZerothDefine.API_WWS_URL);
    }

    // set ASR server url
    public void setPrefAsrUrl(String url) {
        SharedPreferences.Editor edit = mSharedPref.edit();
        edit.putString("SP_KEY_ASR_URL", url);
        edit.apply();
    }

    // get ASR auth server url
    public String getPrefAsrAuthUrl() {
        return mSharedPref.getString("SP_KEY_ASR_AUTH_URL", ZerothDefine.API_AUTH_WWS_URL);
    }

    // set ASR auth server url
    public void setPrefAsrAuthUrl(String url) {
        SharedPreferences.Editor edit = mSharedPref.edit();
        edit.putString("SP_KEY_ASR_AUTH_URL", url);
        edit.apply();
    }

    // get Auth token generate url
    public String getPrefAuthTokenUrl() {
        return mSharedPref.getString("SP_KEY_AUTH_TOKEN_URL", ZerothDefine.API_AUTH_COMPLETE_URL);
    }

    // set Auth token generate url
    public void setPrefAuthTokenUrl(String url) {
        SharedPreferences.Editor edit = mSharedPref.edit();
        edit.putString("SP_KEY_AUTH_TOKEN_URL", url);
        edit.apply();
    }

    // convert  preference index value -> subtitle font size
    public int convertPrefSubtitleFontSize(int i) {
        switch(i) {
            case SUBTITLE_FONT_SIZE_VERY_LARGE:
                return 28;
            case SUBTITLE_FONT_SIZE_LARGE:
                return 26;
            case SUBTITLE_FONT_SIZE_MEDIUM:
                return 24;
            case SUBTITLE_FONT_SIZE_SMALL:
                return 22;
            case SUBTITLE_FONT_SIZE_VERY_SMALL:
                return 20;
            default:
                return 24;
        }
    }

    // convert  preference index value -> subtitle line
    public int convertPrefSubtitleLine(int i) {
        switch(i) {
            case SUBTITLE_LINE_2:
                return 2;
            case SUBTITLE_LINE_3:
                return 3;
            case SUBTITLE_LINE_4:
                return 4;
            default:
                return 3;
        }
    }

    // convert  preference index value -> subtitle font path
    public String convertPrefSubtitleFontPath(Context context, int i) {
        String[] fontPaths = context.getResources().getStringArray(R.array.subtitle_font_list);

        switch(i) {
            case 0:
                return "";
            case 1:
                return fontPaths[0];
            case 2:
                return fontPaths[1];
            case 3:
                return fontPaths[2];
            case 4:
                return fontPaths[3];
        }

        return null;
    }

    // convert  preference index value -> subtitle font display name
    public  String convertPrefSubtitleFontName(Context context, int i) {
        String[] fontPaths = context.getResources().getStringArray(R.array.subtitle_font_list);

        switch(i) {
            case 0:
                return context.getResources().getString(R.string.subtitle_font_0);
            case 1:
                return context.getResources().getString(R.string.subtitle_font_1);
            case 2:
                return context.getResources().getString(R.string.subtitle_font_2);
            case 3:
                return context.getResources().getString(R.string.subtitle_font_3);
            case 4:
                return context.getResources().getString(R.string.subtitle_font_4);
        }

        return null;
    }

    // convert  preference index value -> subtitle font Typeface
    public Typeface convertPrefSubtitleFontTypeface(Context context, int i) {
        String fontPath = convertPrefSubtitleFontPath(context, i);

        if(fontPath != null && !fontPath.equals("")) {
            return Typeface.createFromAsset(context.getAssets(), fontPath);
        }
        else {
            return Typeface.DEFAULT;
        }
    }

    // convert  preference index value -> subtitle background transparency
    public  float convertPrefSubtitleTransparency(int i) {
        if(i < 0 && i > 100) return 0.5f;

        return (float) (100-i)/100.f;
    }


    // 통계 API pref 추가
    // jhong
    // since 210824
    // ################################################################################################

    private final String PREF_KEY_START_TIME_SEQ = "PREF_KEY_START_TIME_SEQ";
    private final String PREF_KEY_INIT_START_TIME_SEQ = "PREF_KEY_INIT_START_TIME_SEQ";
    private final String PREF_KEY_START_URL = "PREF_KEY_START_URL";

    private final String PREF_WEB_ADDINFO_URL = "PREF_WEB_ADDINFO_URL";
    private final String PREF_WEB_NOTICEVIEW_URL = "PREF_WEB_NOTICEVIEW_URL";
    private final String PREF_WEB_FAQVIEW_URL = "PREF_WEB_FAQVIEW_URL";
    private final String PREF_WEB_EVENTVIEW_URL = "PREF_WEB_EVENTVIEW_URL";
    private final String PREF_WEB_PRIVACY_URL = "PREF_WEB_PRIVACY_URL";

    public void clearPrefstatistics() {
        clearPrefStartTimeSeq();
        clearPrefStartURL();
        clearPrefInitStartTimeSeq();
    }

    public void setPrefInitStartTimeSeq(String seq) {
        SharedPreferences.Editor edit = mSharedPref.edit();
        edit.putString(PREF_KEY_INIT_START_TIME_SEQ, seq);
        edit.apply();
    }

    public String getPrefInitStartTimeSeq() {
        return mSharedPref.getString(PREF_KEY_INIT_START_TIME_SEQ, "");
    }

    public void clearPrefInitStartTimeSeq() {
        SharedPreferences.Editor edit = mSharedPref.edit();
        edit.remove(PREF_KEY_INIT_START_TIME_SEQ);
        edit.commit();
    }

    public void setPrefStartTimeSeq(String seq) {
        SharedPreferences.Editor edit = mSharedPref.edit();
        edit.putString(PREF_KEY_START_TIME_SEQ, seq);
        edit.apply();
    }

    public void clearPrefStartTimeSeq() {
        SharedPreferences.Editor edit = mSharedPref.edit();
        edit.remove(PREF_KEY_START_TIME_SEQ);
        edit.commit();
    }

    public String getPrefStartTimeSeq(){
        return mSharedPref.getString(PREF_KEY_START_TIME_SEQ, "");
    }

    public void setPrefStartURL(String url) {
        SharedPreferences.Editor edit = mSharedPref.edit();
        edit.putString(PREF_KEY_START_URL, url);
        edit.apply();
    }

    public String getPrefStartURL(){
        return mSharedPref.getString(PREF_KEY_START_URL, "");
    }

    public void clearPrefStartURL() {
        SharedPreferences.Editor edit = mSharedPref.edit();
        edit.remove(PREF_KEY_START_URL);
        edit.commit();
    }

    /////////////////////////////// WEB INFO ///////////////////////////////
    // get Web server add info url
    public String getPrefWebAddInfoUrl() {
        return mSharedPref.getString(PREF_WEB_ADDINFO_URL, ZerothDefine.API_WEB_URL_ADDINFO);
    }

    // set Web server add info url
    public void setPrefWebAddInfoUrl(String url) {
        SharedPreferences.Editor edit = mSharedPref.edit();
        edit.putString(PREF_WEB_ADDINFO_URL, url);
        edit.apply();
    }

    // get Web server notice view url
    public String getPrefWebNoticeViewUrl() {
        return mSharedPref.getString(PREF_WEB_NOTICEVIEW_URL, ZerothDefine.API_WEB_URL_NOTICEVIEW);
    }

    // get Web server faq view url
    public String getPrefWebFaqViewUrl() {
        return mSharedPref.getString(PREF_WEB_FAQVIEW_URL, ZerothDefine.API_WEB_URL_FAQVIEW);
    }

    // get Web server event view url
    public String getPrefWebEventViewUrl() {
        return mSharedPref.getString(PREF_WEB_EVENTVIEW_URL, ZerothDefine.API_WEB_URL_EVENTVIEW);
    }

    // set Web server add info url
    public void setPrefWebNoticeViewUrl(String url) {
        SharedPreferences.Editor edit = mSharedPref.edit();
        edit.putString(PREF_WEB_NOTICEVIEW_URL, url);
        edit.apply();
    }


    // get Web server privacy policy url
    public String getPrefWebViewPrivacyPolicyUrl() {
        return mSharedPref.getString(PREF_WEB_PRIVACY_URL, ZerothDefine.API_WEB_URL_PRIVACY_POLICY);
    }

    // set Web server privacy policy url
    public void setPrefWebViewPrivacyPolicyUrl(String url) {
        SharedPreferences.Editor edit = mSharedPref.edit();
        edit.putString(PREF_WEB_PRIVACY_URL, url);
        edit.apply();
    }
}
