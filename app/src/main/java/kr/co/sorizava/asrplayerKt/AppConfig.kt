package kr.co.sorizava.asrplayerKt

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.preference.PreferenceManager
import org.mozilla.focus.R

class AppConfig(){

    private var mContext: Context? = null
    private var mSharedPref: SharedPreferences? = null

    constructor(context: Context?) : this() {
        mContext = context
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(mContext)
    }
    companion object
    {

        // 자막 위치
        val SUBTITLE_POSITION_TOP = 0
        val SUBTITLE_POSITION_BOTTOM = 1

        // 자막 폰트 크기
        val SUBTITLE_FONT_SIZE_VERY_SMALL = 0
        val SUBTITLE_FONT_SIZE_SMALL = 1
        val SUBTITLE_FONT_SIZE_MEDIUM = 2
        val SUBTITLE_FONT_SIZE_LARGE = 3
        val SUBTITLE_FONT_SIZE_VERY_LARGE = 4

        // 자막 라인 수
        val SUBTITLE_LINE_2 = 2
        val SUBTITLE_LINE_3 = 3
        val SUBTITLE_LINE_4 = 4



        private var instance: AppConfig? = null

        /**
         * SzApp 에서 init
         *
         * @param context Context
         */
        fun init(context: Context?) {
            if (instance == null) {
                instance = AppConfig(context)
            }
        }
        /**
         * Config instance
         *
         * @return  instance of this class.
         */
        fun getInstance(): AppConfig? {
            return instance
        }
    }





    // get subtitle on/off
    fun getPrefSubtitleOnOff(): Boolean {
        return mSharedPref!!.getBoolean("SP_KEY_SZ_SUBTITLE_ON", true)
    }

    // set subtitle on/off
    fun setPrefSubtitleOnOff(z: Boolean) {
        val edit = mSharedPref!!.edit()
        edit.putBoolean("SP_KEY_SZ_SUBTITLE_ON", z)
        edit.apply()
    }

    // get subtitle sync status
    fun getPrefSubtitleSync(): Boolean {
        return mSharedPref!!.getBoolean("SP_KEY_SZ_SUBTITLE_SYNC", true)
    }

    // set subtitle sync status
    // param boolean: true:noraml sync ,  flase:negative sync (자막 먼저 보기)
    fun setPrefSubtitleSync(z: Boolean) {
        val edit = mSharedPref!!.edit()
        edit.putBoolean("SP_KEY_SZ_SUBTITLE_SYNC", z)
        edit.apply()
    }

    // get subtitle position
    fun getPrefSubtitlePosition(): Int {
        return mSharedPref!!.getInt("SP_KEY_SZ_SUBTITLE_POSITION",
            AppConfig.SUBTITLE_POSITION_BOTTOM) // 화면 아래
    }

    // set subtitle position
    fun setPrefSubtitlePosition(i: Int) {
        val edit = mSharedPref!!.edit()
        edit.putInt("SP_KEY_SZ_SUBTITLE_POSITION", i)
        edit.apply()
    }

    // get subtitle max line
    fun getPrefSubtitleLine(): Int {
        return mSharedPref!!.getInt("SP_KEY_SZ_SUBTITLE_LINE",
            AppConfig.SUBTITLE_LINE_3) // 3줄
    }

    // set subtitle max line
    fun setPrefSubtitleLine(i: Int) {
        val edit = mSharedPref!!.edit()
        edit.putInt("SP_KEY_SZ_SUBTITLE_LINE", i)
        edit.apply()
    }

    // get subtitle font index
    fun getPrefSubtitleFont(): Int {
        return mSharedPref!!.getInt("SP_KEY_SZ_SUBTITLE_FONT", 0) // system default: 0
    }

    // set subtitle font index
    fun setPrefSubtitleFont(i: Int) {
        val edit = mSharedPref!!.edit()
        edit.putInt("SP_KEY_SZ_SUBTITLE_FONT", i)
        edit.apply()
    }

    // get subtitle font size
    fun getPrefSubtitleFontSize(): Int {
        return mSharedPref!!.getInt("SP_KEY_SZ_SUBTITLE_FONT_SIZE",
            AppConfig.SUBTITLE_FONT_SIZE_MEDIUM) // 중간 크기
    }

    // set subtitle font size
    fun setPrefSubtitleFontSize(i: Int) {
        val edit = mSharedPref!!.edit()
        edit.putInt("SP_KEY_SZ_SUBTITLE_FONT_SIZE", i)
        edit.apply()
    }

    // get subtitle foreground color
    fun getPrefSubtitleForegroundColor(): Int {
        return mSharedPref!!.getInt("SP_KEY_SZ_SUBTITLE_FOREGROUND_COLOR", Color.WHITE)
    }

    // set subtitle foreground color
    fun setPrefSubtitleForegroundColor(color: Int) {
        val edit = mSharedPref!!.edit()
        edit.putInt("SP_KEY_SZ_SUBTITLE_FOREGROUND_COLOR", color)
        edit.apply()
    }

    // get subtitle background transparency
    fun getPrefSubtitleTransparency(): Int {
        return mSharedPref!!.getInt("SP_KEY_SZ_SUBTITLE_TRANSPARENCY", 50) // 0.5f (0-100:0~1)
    }

    // set subtitle background transparency
    fun setPrefSubtitleTransparency(i: Int) {
        val edit = mSharedPref!!.edit()
        edit.putInt("SP_KEY_SZ_SUBTITLE_TRANSPARENCY", i)
        edit.apply()
    }

    // get asr auth connection
    fun getPrefAsrAuthConnect(): Boolean {
        return mSharedPref!!.getBoolean("SP_KEY_ASR_AUTH_CONNECT", false)
    }

    // set asr auth connection
    fun setPrefAsrAuthConnect(z: Boolean) {
        val edit = mSharedPref!!.edit()
        edit.putBoolean("SP_KEY_ASR_AUTH_CONNECT", z)
        edit.apply()
    }

    // get asr connection use param project
    fun getPrefAsrUseParamProject(): Boolean {
        return mSharedPref!!.getBoolean("SP_KEY_ASR_USE_PARAM_PROJECT", true)
    }

    // set asr connection use param project
    fun setPrefAsrUseParamProject(z: Boolean) {
        val edit = mSharedPref!!.edit()
        edit.putBoolean("SP_KEY_ASR_USE_PARAM_PROJECT", z)
        edit.apply()
    }

    // get ASR server model
    fun getPrefAsrModel(): String? {
        return mSharedPref!!.getString("SP_KEY_ASR_SERVER_MODEL", ZerothDefine.API_WWS_MODEL)
    }

    // set ASR server model
    fun setPrefAsrModel(name: String?) {
        val edit = mSharedPref!!.edit()
        edit.putString("SP_KEY_ASR_SERVER_MODEL", name)
        edit.apply()
    }

    // get app key
    fun getPrefAppKey(): String? {
        return mSharedPref!!.getString("SP_KEY_ASR_APP_KEY", ZerothDefine.API_APP_KEY)
    }

    // set app key
    fun setPrefAppKey(id: String?) {
        val edit = mSharedPref!!.edit()
        edit.putString("SP_KEY_ASR_APP_KEY", id)
        edit.apply()
    }

    // get app secret
    fun getPrefAppSecret(): String? {
        return mSharedPref!!.getString("SP_KEY_ASR_APP_SECRET", ZerothDefine.API_APP_SECRET)
    }

    // set app secret
    fun setPrefAppSecret(key: String?) {
        val edit = mSharedPref!!.edit()
        edit.putString("SP_KEY_ASR_APP_SECRET", key)
        edit.apply()
    }

    // get ASR server url
    fun getPrefAsrUrl(): String? {
        return mSharedPref!!.getString("SP_KEY_ASR_URL", ZerothDefine.API_WWS_URL)
    }

    // set ASR server url
    fun setPrefAsrUrl(url: String?) {
        val edit = mSharedPref!!.edit()
        edit.putString("SP_KEY_ASR_URL", url)
        edit.apply()
    }

    // get ASR auth server url
    fun getPrefAsrAuthUrl(): String? {
        return mSharedPref!!.getString("SP_KEY_ASR_AUTH_URL", ZerothDefine.API_AUTH_WWS_URL)
    }

    // set ASR auth server url
    fun setPrefAsrAuthUrl(url: String?) {
        val edit = mSharedPref!!.edit()
        edit.putString("SP_KEY_ASR_AUTH_URL", url)
        edit.apply()
    }

    // get Auth token generate url
    fun getPrefAuthTokenUrl(): String? {
        return mSharedPref!!.getString("SP_KEY_AUTH_TOKEN_URL", ZerothDefine.API_AUTH_COMPLETE_URL)
    }

    // set Auth token generate url
    fun setPrefAuthTokenUrl(url: String?) {
        val edit = mSharedPref!!.edit()
        edit.putString("SP_KEY_AUTH_TOKEN_URL", url)
        edit.apply()
    }

    // convert  preference index value -> subtitle font size
    fun convertPrefSubtitleFontSize(i: Int): Int {
        return when (i) {
            AppConfig.SUBTITLE_FONT_SIZE_VERY_LARGE -> 28
            AppConfig.SUBTITLE_FONT_SIZE_LARGE -> 26
            AppConfig.SUBTITLE_FONT_SIZE_MEDIUM -> 24
            AppConfig.SUBTITLE_FONT_SIZE_SMALL -> 22
            AppConfig.SUBTITLE_FONT_SIZE_VERY_SMALL -> 20
            else -> 24
        }
    }

    // convert  preference index value -> subtitle line
    fun convertPrefSubtitleLine(i: Int): Int {
        return when (i) {
            AppConfig.SUBTITLE_LINE_2 -> 2
            AppConfig.SUBTITLE_LINE_3 -> 3
            AppConfig.SUBTITLE_LINE_4 -> 4
            else -> 3
        }
    }

    // convert  preference index value -> subtitle font path
    fun convertPrefSubtitleFontPath(context: Context, i: Int): String? {
        val fontPaths = context.resources.getStringArray(R.array.subtitle_font_list)
        when (i) {
            0 -> return ""
            1 -> return fontPaths[0]
            2 -> return fontPaths[1]
            3 -> return fontPaths[2]
            4 -> return fontPaths[3]
        }
        return null
    }

    // convert  preference index value -> subtitle font display name
    fun convertPrefSubtitleFontName(context: Context, i: Int): String? {
        val fontPaths = context.resources.getStringArray(R.array.subtitle_font_list)
        when (i) {
            0 -> return context.resources.getString(R.string.subtitle_font_0)
            1 -> return context.resources.getString(R.string.subtitle_font_1)
            2 -> return context.resources.getString(R.string.subtitle_font_2)
            3 -> return context.resources.getString(R.string.subtitle_font_3)
            4 -> return context.resources.getString(R.string.subtitle_font_4)
        }
        return null
    }

    // convert  preference index value -> subtitle font Typeface
    fun convertPrefSubtitleFontTypeface(context: Context, i: Int): Typeface? {
        val fontPath = convertPrefSubtitleFontPath(context, i)
        return if (fontPath != null && fontPath != "") {
            Typeface.createFromAsset(context.assets, fontPath)
        } else {
            Typeface.DEFAULT
        }
    }

    // convert  preference index value -> subtitle background transparency
    fun convertPrefSubtitleTransparency(i: Int): Float {
        return if (i < 0 && i > 100) 0.5f else (100 - i).toFloat() / 100f
    }


    // 통계 API pref 추가
    // jhong
    // since 210824
    // ################################################################################################
    private val PREF_KEY_START_TIME_SEQ = "PREF_KEY_START_TIME_SEQ"
    private val PREF_KEY_INIT_START_TIME_SEQ = "PREF_KEY_INIT_START_TIME_SEQ"
    private val PREF_KEY_START_URL = "PREF_KEY_START_URL"

    private val PREF_WEB_ADDINFO_URL = "PREF_WEB_ADDINFO_URL"
    private val PREF_WEB_NOTICEVIEW_URL = "PREF_WEB_NOTICEVIEW_URL"
    private val PREF_WEB_FAQVIEW_URL = "PREF_WEB_FAQVIEW_URL";
    private val PREF_WEB_EVENTVIEW_URL = "PREF_WEB_EVENTVIEW_URL";
    private val PREF_WEB_PRIVACY_URL = "PREF_WEB_PRIVACY_URL"

    fun clearPrefstatistics() {
        clearPrefStartTimeSeq()
        clearPrefStartURL()
        clearPrefInitStartTimeSeq()
    }

    fun setPrefInitStartTimeSeq(seq: String?) {
        val edit = mSharedPref!!.edit()
        edit.putString(PREF_KEY_INIT_START_TIME_SEQ, seq)
        edit.apply()
    }

    fun getPrefInitStartTimeSeq(): String? {
        return mSharedPref!!.getString(PREF_KEY_INIT_START_TIME_SEQ, "")
    }

    fun clearPrefInitStartTimeSeq() {
        val edit = mSharedPref!!.edit()
        edit.remove(PREF_KEY_INIT_START_TIME_SEQ)
        edit.commit()
    }

    fun setPrefStartTimeSeq(seq: String?) {
        val edit = mSharedPref!!.edit()
        edit.putString(PREF_KEY_START_TIME_SEQ, seq)
        edit.apply()
    }

    fun clearPrefStartTimeSeq() {
        val edit = mSharedPref!!.edit()
        edit.remove(PREF_KEY_START_TIME_SEQ)
        edit.commit()
    }

    fun getPrefStartTimeSeq(): String? {
        return mSharedPref!!.getString(PREF_KEY_START_TIME_SEQ, "")
    }

    fun setPrefStartURL(url: String?) {
        val edit = mSharedPref!!.edit()
        edit.putString(PREF_KEY_START_URL, url)
        edit.apply()
    }

    fun getPrefStartURL(): String? {
        return mSharedPref!!.getString(PREF_KEY_START_URL, "")
    }

    fun clearPrefStartURL() {
        val edit = mSharedPref!!.edit()
        edit.remove(PREF_KEY_START_URL)
        edit.commit()
    }

    /////////////////////////////// WEB INFO ///////////////////////////////
    // get Web server add info url
    fun getPrefWebAddInfoUrl(): String? {
        return mSharedPref!!.getString(PREF_WEB_ADDINFO_URL, ZerothDefine.API_WEB_URL_ADDINFO)
    }

    // set Web server add info url
    fun setPrefWebAddInfoUrl(url: String?) {
        val edit = mSharedPref!!.edit()
        edit.putString(PREF_WEB_ADDINFO_URL, url)
        edit.apply()
    }

    // get Web server add info url
    fun getPrefWebNoticeViewUrl(): String? {
        return mSharedPref!!.getString(PREF_WEB_NOTICEVIEW_URL, ZerothDefine.API_WEB_URL_NOTICEVIEW)
    }

    // get Web server faq view url
    fun getPrefWebFaqViewUrl(): String? {
        return mSharedPref!!.getString(PREF_WEB_FAQVIEW_URL, ZerothDefine.API_WEB_URL_FAQVIEW)
    }

    // get Web server event view url
    fun getPrefWebEventViewUrl(): String? {
        return mSharedPref!!.getString(PREF_WEB_EVENTVIEW_URL, ZerothDefine.API_WEB_URL_EVENTVIEW)
    }




    // set Web server add info url
    fun setPrefWebNoticeViewUrl(url: String?) {
        val edit = mSharedPref!!.edit()
        edit.putString(PREF_WEB_NOTICEVIEW_URL, url)
        edit.apply()
    }


    // get Web server privacy policy url
    fun getPrefWebViewPrivacyPolicyUrl(): String? {
        return mSharedPref!!.getString(PREF_WEB_PRIVACY_URL,
            ZerothDefine.API_WEB_URL_PRIVACY_POLICY)
    }

    // set Web server privacy policy url
    fun setPrefWebViewPrivacyPolicyUrl(url: String?) {
        val edit = mSharedPref!!.edit()
        edit.putString(PREF_WEB_PRIVACY_URL, url)
        edit.apply()
    }

}