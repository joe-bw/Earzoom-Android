/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.focus.activity

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.sdk.user.UserApiClient
import com.sorizava.asrplayer.config.SorizavaLoginManager
import com.sorizava.asrplayer.config.StatisticsConstants.CASE_URL_JTBC
import com.sorizava.asrplayer.config.StatisticsConstants.CASE_URL_KBS
import com.sorizava.asrplayer.config.StatisticsConstants.CASE_URL_MBC
import com.sorizava.asrplayer.config.StatisticsConstants.CASE_URL_MBN
import com.sorizava.asrplayer.config.StatisticsConstants.CASE_URL_NAVER
import com.sorizava.asrplayer.config.StatisticsConstants.CASE_URL_SBS
import com.sorizava.asrplayer.config.StatisticsConstants.CASE_URL_YNA
import com.sorizava.asrplayer.config.StatisticsConstants.CASE_URL_YOUTUBE
import com.sorizava.asrplayer.config.StatisticsConstants.CASE_URL_YTN
import com.sorizava.asrplayer.config.StatisticsConstants.URL_ETC
import com.sorizava.asrplayer.config.StatisticsConstants.URL_JTBC
import com.sorizava.asrplayer.config.StatisticsConstants.URL_KBS
import com.sorizava.asrplayer.config.StatisticsConstants.URL_MBC
import com.sorizava.asrplayer.config.StatisticsConstants.URL_MBN
import com.sorizava.asrplayer.config.StatisticsConstants.URL_NAVER
import com.sorizava.asrplayer.config.StatisticsConstants.URL_SBS
import com.sorizava.asrplayer.config.StatisticsConstants.URL_YNA
import com.sorizava.asrplayer.config.StatisticsConstants.URL_YOUTUBE
import com.sorizava.asrplayer.config.StatisticsConstants.URL_YTN
import com.sorizava.asrplayer.data.dto.StartStatisticsDataVO
import com.sorizava.asrplayer.data.vo.EndStatisticsRequest
import com.sorizava.asrplayer.data.vo.LoginDataVO
import com.sorizava.asrplayer.data.vo.LogoutRequest
import com.sorizava.asrplayer.data.vo.StartStatisticsRequest
import com.sorizava.asrplayer.extension.handleFocus
import com.sorizava.asrplayer.extension.hideKeyboard
import com.sorizava.asrplayer.network.AppApiClient
import com.sorizava.asrplayer.network.AppApiResponse
import kr.co.sorizava.asrplayer.AppConfig
import kr.co.sorizava.asrplayer.ZerothDefine
import kr.co.sorizava.asrplayer.websocket.WsManager
import kr.co.sorizava.asrplayer.websocket.WsStatus
import kr.co.sorizava.asrplayer.websocket.listener.WsStatusListener
import mozilla.components.concept.engine.EngineView
import mozilla.components.lib.crash.Crash
import mozilla.components.support.utils.SafeIntent
import org.mozilla.focus.R
import org.mozilla.focus.biometrics.Biometrics
import org.mozilla.focus.ext.components
import org.mozilla.focus.fragment.BrowserFragment
import org.mozilla.focus.fragment.UrlInputFragment
import org.mozilla.focus.locale.LocaleAwareAppCompatActivity
import org.mozilla.focus.navigation.MainActivityNavigation
import org.mozilla.focus.navigation.Navigator
import org.mozilla.focus.session.IntentProcessor
import org.mozilla.focus.session.ui.TabSheetFragment
import org.mozilla.focus.shortcut.HomeScreen
import org.mozilla.focus.state.AppAction
import org.mozilla.focus.state.Screen
import org.mozilla.focus.telemetry.TelemetryWrapper
import org.mozilla.focus.utils.Settings
import org.mozilla.focus.utils.SupportUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

@Suppress("TooManyFunctions")
open class MainActivity : LocaleAwareAppCompatActivity(), WsStatusListener {

  //  private var audioMonitorThread: AudioMonitorThread? = null

    private val intentProcessor by lazy {
        IntentProcessor(this, components.tabsUseCases, components.customTabsUseCases)
    }

    private val navigator by lazy { Navigator(components.appStore, MainActivityNavigation(this)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Light mode 로 동작
        // since 220801
        // jhong
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        if (!isTaskRoot) {
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN == intent.action) {
                finish()
                return
            }
        }

        @Suppress("DEPRECATION") // https://github.com/mozilla-mobile/focus-android/issues/5016
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        setContentView(R.layout.activity_main)

        val intent = SafeIntent(intent)

        if (intent.hasExtra(HomeScreen.ADD_TO_HOMESCREEN_TAG)) {
            intentProcessor.handleNewIntent(this, intent)
        }

        if (intent.isLauncherIntent) {
            TelemetryWrapper.openFromIconEvent()
        }

        val launchCount = Settings.getInstance(this).getAppLaunchCount()
        PreferenceManager.getDefaultSharedPreferences(this)
            .edit()
            .putInt(getString(R.string.app_launch_count), launchCount + 1)
            .apply()

        lifecycle.addObserver(navigator)

        WsManager.getInstance().configure(48000, 2)
        WsManager.getInstance().setListener(this, this)


      //  audioMonitorThread = AudioMonitorThread()
      //  audioMonitorThread!!.start()




    }
/*
    override fun onDestroy() {
        Log.e("MainActivity", "##onDestroy")

        if (audioMonitorThread != null)
        {
            audioMonitorThread!!.stopThread()
            audioMonitorThread = null
        }

        super.onDestroy()
    }
*/

    override fun applyLocale() {
        // We don't care here: all our fragments update themselves as appropriate
    }

    override fun onResume() {
        Log.e("TEST", "##onResume")
        super.onResume()
/*
        val focusApplication = applicationContext as FocusApplication


        if(focusApplication.goBookmarkUrl.isNotEmpty()) {
            components.tabsUseCases.removeAllTabs()
            components.tabsUseCases.addTab(
                focusApplication.goBookmarkUrl,
                source = SessionState.Source.Internal.Menu,
                selectTab = true,
                private = true
            )
        }

        focusApplication.goBookmarkUrl = ""
*/
        WsManager.getInstance().startConnect()

        TelemetryWrapper.startSession()
        checkBiometricStillValid()
    }

    override fun onPause() {
        Log.e("TEST", "##onPause")
        WsManager.getInstance().stopConnect()

        val fragmentManager = supportFragmentManager
        val browserFragment =
            fragmentManager.findFragmentByTag(BrowserFragment.FRAGMENT_TAG) as BrowserFragment?
        browserFragment?.cancelAnimation()

        val urlInputFragment =
            fragmentManager.findFragmentByTag(UrlInputFragment.FRAGMENT_TAG) as UrlInputFragment?
        urlInputFragment?.cancelAnimation()

        super.onPause()

        TelemetryWrapper.stopSession()
    }

    override fun onStop() {
        super.onStop()

        TelemetryWrapper.stopMainActivity()
    }

    override fun onNewIntent(unsafeIntent: Intent) {
        if (Crash.isCrashIntent(unsafeIntent)) {
            val browserFragment = supportFragmentManager
                .findFragmentByTag(BrowserFragment.FRAGMENT_TAG) as BrowserFragment?
            val crash = Crash.fromIntent(unsafeIntent)

            browserFragment?.handleTabCrash(crash)
        }

        val intent = SafeIntent(unsafeIntent)

        if (intent.dataString.equals(SupportUtils.OPEN_WITH_DEFAULT_BROWSER_URL)) {
            components.appStore.dispatch(
                AppAction.OpenSettings(
                    page = Screen.Settings.Page.General
                )
            )
            super.onNewIntent(unsafeIntent)
            return
        }

        val action = intent.action

        if (intent.hasExtra(HomeScreen.ADD_TO_HOMESCREEN_TAG)) {
            intentProcessor.handleNewIntent(this, intent)
        }

        if (ACTION_OPEN == action) {
            TelemetryWrapper.openNotificationActionEvent()
        }

        if (ACTION_ERASE == action) {
            processEraseAction(intent)
        }

        if (intent.isLauncherIntent) {
            TelemetryWrapper.resumeFromIconEvent()
        }

        super.onNewIntent(unsafeIntent)
    }

    private fun processEraseAction(intent: SafeIntent) {
        val fromShortcut = intent.getBooleanExtra(EXTRA_SHORTCUT, false)
        val fromNotification = intent.getBooleanExtra(EXTRA_NOTIFICATION, false)

        components.tabsUseCases.removeAllTabs()

        if (fromShortcut) {
            TelemetryWrapper.eraseShortcutEvent()
        } else if (fromNotification) {
            TelemetryWrapper.eraseAndOpenNotificationActionEvent()
        }
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return if (name == EngineView::class.java.name) {
            components.engine.createView(context, attrs).asView()
        } else super.onCreateView(name, context, attrs)
    }

    override fun onBackPressed() {
        val fragmentManager = supportFragmentManager

        val sessionsSheetFragment = fragmentManager.findFragmentByTag(
            TabSheetFragment.FRAGMENT_TAG
        ) as TabSheetFragment?
        if (sessionsSheetFragment != null &&
            sessionsSheetFragment.isVisible &&
            sessionsSheetFragment.onBackPressed()
        ) {
            // SessionsSheetFragment handles back presses itself (custom animations).
            return
        }

        val urlInputFragment = fragmentManager.findFragmentByTag(UrlInputFragment.FRAGMENT_TAG) as UrlInputFragment?
        if (urlInputFragment != null &&
            urlInputFragment.isVisible &&
            urlInputFragment.onBackPressed()
        ) {
            // The URL input fragment has handled the back press. It does its own animations so
            // we do not try to remove it from outside.
            return
        }

        val browserFragment = fragmentManager.findFragmentByTag(BrowserFragment.FRAGMENT_TAG) as BrowserFragment?
        if (browserFragment != null &&
            browserFragment.isVisible &&
            browserFragment.onBackPressed()
        ) {
            // The Browser fragment handles back presses on its own because it might just go back
            // in the browsing history.

//            browserFragment.onCheckStartURL()
            browserFragment.onCallEndTime()
            return
        }


        val appStore = components.appStore
        if (appStore.state.screen is Screen.Settings) {
            // When on a settings screen we want the same behavior as navigating "up" via the toolbar
            // and therefore dispatch the `NavigateUp` action on the app store.
            val selectedTabId = components.store.state.selectedTabId
            appStore.dispatch(AppAction.NavigateUp(selectedTabId))
            return
        }

        super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            // We forward an up action to the app store with the NavigateUp action to let the reducer
            // decide to show a different screen.
            val selectedTabId = components.store.state.selectedTabId
            components.appStore.dispatch(AppAction.NavigateUp(selectedTabId))
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    // Handles the edge case of a user removing all enrolled prints while auth was enabled
    private fun checkBiometricStillValid() {
        // Disable biometrics if the user is no longer eligible due to un-enrolling fingerprints:
        if (!Biometrics.hasFingerprintHardware(this)) {
            PreferenceManager.getDefaultSharedPreferences(this)
                .edit().putBoolean(
                    getString(R.string.pref_key_biometric),
                    false
                ).apply()
        }
    }

    companion object {
        const val ACTION_ERASE = "erase"
        const val ACTION_OPEN = "open"

        const val EXTRA_NOTIFICATION = "notification"

        private const val EXTRA_SHORTCUT = "shortcut"

        private const val TAG = "TEST"
    }

    private var mASRServerConnectionStatus = WsStatus.DISCONNECTED; //SorizavaManagerListener.STATUS_DISCONNECTED

    override fun onConnectionStatusChanged(status: Int) {
        Log.e("TEST", "onConnectionStatusChanged: $status")
        // ì°ê²° ì¤ì¼ë ì°ê²° ì¤í¨íë©´
        /*
        if (mASRServerConnectionStatus == SorizavaManagerListener.STATUS_CONNECTING &&
                status == SorizavaManagerListener.STATUS_CONNECTION_FAILED) {
              Log.w(TAG, "onConnectionStatusChanged: " + status);
            //   if(SorizavaManagerListener.getInstance().getRetryConnectionCount() < 2) {  // ê³ì ë³´ì¬ì¤ íìê° ìê¸° ëë¬¸ì~
            //       Log.d(LOGTAG, "Failed to connection ASR server");
            //showConfirmDialog(R.string.dialog_title_error,  R.string.asr_server_connection_error);
            //   }
        }

         */
        mASRServerConnectionStatus = status
    }

    override fun onMessageSubtitle(msg: String) {
        val fragmentManager = supportFragmentManager
        val browserFragment = fragmentManager.findFragmentByTag(BrowserFragment.FRAGMENT_TAG) as BrowserFragment?

        browserFragment?.onMessageSubtitle(msg)
    }

    override fun onMessageSubtitle(msg: String, final: Boolean) {
    }

    override fun resetSubtitleView() {
        val fragmentManager = supportFragmentManager
        val browserFragment = fragmentManager.findFragmentByTag(BrowserFragment.FRAGMENT_TAG) as BrowserFragment?

        browserFragment?.resetSubtitleView(true)
    }

    // url 추적 로직 추가
    // jhong
    // ####################################################
    fun checkStartURL(url: String) {
        Log.d("TEST", "checkStartURL()")
        checkStartTime(url)
    }

    private fun checkStartTime(url: String) {
        if (TextUtils.isEmpty(AppConfig.getInstance().prefStartTimeSeq)){
            callStartTime(url)
        } else {
            if (url == AppConfig.getInstance().prefStartURL){
                return
            } else {
                callEndAndStartTime(url)
            }
        }
    }

    fun callStartTime(url: String) {

        val caseUrl = checkCategoryOrEtc(url)

        val id = SorizavaLoginManager.instance?.prefUserId ?: return

        val currentTime = Calendar.getInstance().time

        val connDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(currentTime)
        val stTime = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(currentTime)

        Log.d("TEST", "stTime: $stTime")

        val request = StartStatisticsRequest(
            id,
            connDate,
            stTime,
            caseUrl
        )

        val call = AppApiClient.apiService.requestStartStatistics(request)
        call.enqueue(object : Callback<AppApiResponse<StartStatisticsDataVO>> {

            override fun onResponse(
                call: Call<AppApiResponse<StartStatisticsDataVO>>,
                response: Response<AppApiResponse<StartStatisticsDataVO>>
            ) {
                Log.d(TAG, "callStartTime - response.code(): " + response.code())
                if (response.isSuccessful) {
                    Log.d(TAG, "callStartTime - isSuccessful")
                    val result: AppApiResponse<*> = response.body()!!
                    Log.d(TAG, "callStartTime - onResponse - result: $result")
                } else {
                    Log.d(TAG, "callStartTime - fail")
                }
            }

            override fun onFailure(
                call: Call<AppApiResponse<StartStatisticsDataVO>>,
                t: Throwable
            ) {
                Log.d(TAG, "callStartTime - onFailure - result: " + t.message)
            }
        })
    }

    fun callEndAndStartTime(url: String) {

        val statisticsSeq = AppConfig.getInstance().prefStartTimeSeq

        if (TextUtils.isEmpty(statisticsSeq)) {
//            callStartTime(url)
            return
        }

        Log.d(TAG, "statisticsSeq: $statisticsSeq")

        val currentTime = Calendar.getInstance().time

        val endTime = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(currentTime)

        val request = EndStatisticsRequest(
            statisticsSeq,
            endTime
        )

        val call = AppApiClient.apiService.requestEndStatistics(request)
        call.enqueue(object : Callback<AppApiResponse<Int>> {
            override fun onResponse(call: Call<AppApiResponse<Int>>, response: Response<AppApiResponse<Int>>) {
                Log.d(TAG, "callEndAndStartTime - response.code(): " + response.code())
                if (response.isSuccessful) {
                    Log.d(TAG, "callEndAndStartTime - isSuccessful")
                    val result: AppApiResponse<*> = response.body()!!
                    Log.d(TAG, "callEndAndStartTime - onResponse - result: $result")

                    if (result.status == 200) {
                        callStartTime(url)
                    }
                } else {
                    Log.d(TAG, "callEndAndStartTime - fail")
                }
            }

            override fun onFailure(call: Call<AppApiResponse<Int>>, t: Throwable) {
                Log.d(TAG, "callEndAndStartTime - onFailure - result: " + t.message)
            }
        })
    }

    fun callEndTime() {

        val statisticsSeq = AppConfig.getInstance().prefStartTimeSeq

        val currentTime = Calendar.getInstance().time

        val endTime = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(currentTime)

        val request = EndStatisticsRequest(
            statisticsSeq,
            endTime
        )

        val call = AppApiClient.apiService.requestEndStatistics(request)
        call.enqueue(object : Callback<AppApiResponse<Int>> {
            override fun onResponse(call: Call<AppApiResponse<Int>>, response: Response<AppApiResponse<Int>>) {
                Log.d(TAG, "callEndTime - response.code(): " + response.code())
                if (response.isSuccessful) {
                    Log.d(TAG, "callEndTime - isSuccessful")
                    val result: AppApiResponse<*> = response.body()!!
                    Log.d(TAG, "callEndTime - onResponse - result: $result")

//                    AppConfig.getInstance().clearPrefStartTimeSeq()
                } else {
                    Log.d(TAG, "callEndTime - fail")
                }
            }

            override fun onFailure(call: Call<AppApiResponse<Int>>, t: Throwable) {
                Log.d(TAG, "callEndTime - onFailure - result: " + t.message)
            }
        })
    }

    fun callInitEndTime() {

        val statisticsSeq = AppConfig.getInstance().prefInitStartTimeSeq

        val currentTime = Calendar.getInstance().time

        val endTime = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(currentTime)

        val request = EndStatisticsRequest(
            statisticsSeq,
            endTime
        )

        val call = AppApiClient.apiService.requestEndStatistics(request)
        call.enqueue(object : Callback<AppApiResponse<Int>> {
            override fun onResponse(call: Call<AppApiResponse<Int>>, response: Response<AppApiResponse<Int>>) {
                Log.d(TAG, "callInitEndTime - response.code(): " + response.code())
                if (response.isSuccessful) {
                    Log.d(TAG, "callInitEndTime - isSuccessful")
                    val result: AppApiResponse<*> = response.body()!!
                    Log.d(TAG, "callInitEndTime - onResponse - result: $result")

                    AppConfig.getInstance().clearPrefstatistics()
                } else {
                    Log.d(TAG, "callInitEndTime - fail")
                }
            }

            override fun onFailure(call: Call<AppApiResponse<Int>>, t: Throwable) {
                Log.d(TAG, "callInitEndTime - onFailure - result: " + t.message)
            }
        })
    }

    private fun checkCategoryOrEtc(url: String): String {

        lateinit var returnUrl: String

        if (url.contains(CASE_URL_YOUTUBE)){
            returnUrl = URL_YOUTUBE
        } else if (url.contains(CASE_URL_YTN)) {
            returnUrl = URL_YTN
        } else if (url.contains(CASE_URL_KBS)) {
            returnUrl = URL_KBS
        } else if (url.contains(CASE_URL_SBS)) {
            returnUrl = URL_SBS
        } else if (url.contains(CASE_URL_MBC)) {
            returnUrl = URL_MBC
        } else if (url.contains(CASE_URL_YNA)) {
            returnUrl = URL_YNA
        } else if (url.contains(CASE_URL_JTBC)) {
            returnUrl = URL_JTBC
        } else if (url.contains(CASE_URL_MBN)) {
            returnUrl = URL_MBN
        } else if (url.contains(CASE_URL_NAVER)) {
            returnUrl = URL_NAVER
        } else {
            returnUrl = URL_ETC
        }

        return returnUrl
    }

    fun callDialogSignout() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.txt_notice))
            .setCancelable(false)
            .setMessage(getString(R.string.txt_signout))
            .setPositiveButton(getString(R.string.btn_logout)) { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
                callLogout()
            }
            .setNegativeButton(getString(R.string.btn_continuous)) { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }
        val alertDialog = builder.create()
        alertDialog.show()

    }

    private fun callLogout() {

        val userID = SorizavaLoginManager.instance?.prefUserId

        val request = userID?.let { LogoutRequest(it) }

        val call = request?.let { AppApiClient.apiService.requestLogout(it) }
        call?.enqueue(object : Callback<AppApiResponse<LoginDataVO>> {
            override fun onResponse(
                call: Call<AppApiResponse<LoginDataVO>>,
                response: Response<AppApiResponse<LoginDataVO>>
            ) {
                Log.d(TAG, "response.code(): " + response.code())
                Log.d(TAG, "response.body(): " + response.body())
                if (response.isSuccessful) {
                    Log.d(TAG, "isSuccessful")
                    val result: AppApiResponse<*> = response.body()!!
                    Log.d(TAG, "onResponse - result: $result")

                    /** FCM 구독 설정 해제 */
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(ZerothDefine.FCM_SUBSCRIBE_NAME)

                    SorizavaLoginManager.instance?.userSNSType?.let { onSNSLogout(it) }

                    SorizavaLoginManager.instance?.clear()

//                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
//                    finish()

                } else {
                    Log.d(TAG, "fail")
                }
            }

            override fun onFailure(call: Call<AppApiResponse<LoginDataVO>>, t: Throwable) {
                Log.d(TAG, "onFailure - result: " + t.message)
            }
        })
    }

    /**
     * SNS 로그아웃 처리
     *
     * NAVER = 0
     *
     * KAKAO = 1
     *
     * FACEBOOK = 2
     *
     * GOOGLE = 3
     */
    private fun onSNSLogout(snsType : Int) {

        when(snsType) {
//            0 -> OAuthLogin.getInstance().logout(this)

            1 -> UserApiClient.instance.logout { error ->
                    if (error != null) {
                        Log.e(TAG, "로그아웃 실패. SDK에서 토큰 삭제됨", error)
                    }
                    else {
                        Log.i(TAG, "로그아웃 성공. SDK에서 토큰 삭제됨")
                    }
                }

            2 -> com.facebook.login.LoginManager.getInstance().logOut()

            3 -> FirebaseAuth.getInstance().signOut()
        }
    }

    /** 검색어 뷰 이외 터치시 키보드 닫기 */
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val focusView = currentFocus
        if (focusView != null && focusView is EditText) {
            val rect = Rect()
            focusView.getGlobalVisibleRect(rect)
            val x = ev.x.toInt()
            val y = ev.y.toInt()
            if (!rect.contains(x, y)) {
                focusView.hideKeyboard()
                focusView.clearFocus()
            } else {
                focusView.handleFocus()
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}
