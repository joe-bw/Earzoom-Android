/* -*- Mode: Java; c-basic-offset: 4; tab-width: 20; indent-tabs-mode: nil; -*-
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.focus

//import org.mozilla.focus.utils.AdjustHelper
import android.app.Application
import android.content.pm.PackageManager
import android.os.StrictMode
import android.text.TextUtils
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.preference.PreferenceManager
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.sdk.common.KakaoSdk
import com.sorizava.asrplayer.application.FBRemoteConfigManager
import com.sorizava.asrplayer.config.SorizavaLoginManager
import com.sorizava.asrplayer.extension.getVersion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kr.co.sorizava.asrplayerKt.AppConfig
import kr.co.sorizava.asrplayerKt.ZerothDefine
import mozilla.components.browser.state.state.SessionState
import mozilla.components.support.base.facts.register
import mozilla.components.support.base.log.Log
import mozilla.components.support.base.log.sink.AndroidLogSink
import mozilla.components.support.ktx.android.content.isMainProcess
import mozilla.components.support.webextensions.WebExtensionSupport
import org.mozilla.focus.activity.AudioMonitorThread
import org.mozilla.focus.biometrics.LockObserver
import org.mozilla.focus.locale.LocaleAwareApplication
import org.mozilla.focus.navigation.StoreLink
import org.mozilla.focus.session.VisibilityLifeCycleCallback
import org.mozilla.focus.telemetry.FactsProcessor
import org.mozilla.focus.telemetry.ProfilerMarkerFactProcessor
import org.mozilla.focus.telemetry.TelemetryWrapper
import org.mozilla.focus.utils.AppConstants
import kotlin.coroutines.CoroutineContext


/**
 * 기존 firefox 에서 사용하고 있는 config 들 이 있다.
 *
 * 아래와 같은 각 종 config 설정을 application 에서 미리 설정하여 사용한다.
 *  - 앱 버전 확인
 *  - Firebase remote config manager
 *
 */
open class FocusApplication : LocaleAwareApplication(), CoroutineScope {
    lateinit var goBookmarkUrl: String

    private var job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    open val components: Components by lazy { Components(this) }

    var visibilityLifeCycleCallback: VisibilityLifeCycleCallback? = null
        private set

    private val storeLink by lazy { StoreLink(components.appStore, components.store) }
    private val lockObserver by lazy { LockObserver(this, components.store, components.appStore) }

    private var audioMonitorThread: AudioMonitorThread? = null

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private var appVersion: String? = null

    private val fbRemoteConfigManager = FBRemoteConfigManager()

    override fun onCreate() {
        super.onCreate()
        goBookmarkUrl = ""

        Log.addSink(AndroidLogSink("Focus"))
        components.crashReporter.install(this)


        if (isMainProcess()) {
            AppConfig.init(this)

            startAudioMonitor()

            PreferenceManager.setDefaultValues(this, R.xml.settings, false)

            components.engine.warmUp()

            TelemetryWrapper.init(this)
            components.metrics.initialize(this)
            FactsProcessor.initialize()
            ProfilerMarkerFactProcessor.create { components.engine.profiler }.register()

            enableStrictMode()

            // Adjust token 필요
            //AdjustHelper.setupAdjustIfNeeded(this@FocusApplication)

            visibilityLifeCycleCallback = VisibilityLifeCycleCallback(this@FocusApplication)
            registerActivityLifecycleCallbacks(visibilityLifeCycleCallback)

            storeLink.start()

            initializeWebExtensionSupport()

            ProcessLifecycleOwner.get().lifecycle.addObserver(lockObserver)


            ////////////////////////////////////
            //           추가 개발 건           //
            ////////////////////////////////////

            // 버전 네임 확인
            getAppVersionName()

            // Firebase Remote 확인
            getFbRemoteConfig()

            // jhong - kakao init
            KakaoSdk.init(this, getString(R.string.kakao_app_key))

            // jhong - facebook init
            FacebookSdk.sdkInitialize(applicationContext)
            AppEventsLogger.activateApp(this);

            // jhong - 로그인매니저(토큰 등 관리)
            SorizavaLoginManager.onInit(this@FocusApplication)

            // jhong - google init
            FirebaseApp.initializeApp(this)

//            firebaseAnalytics = Firebase.analytics
        }
    }

    private fun getFbRemoteConfig() {
        fbRemoteConfigManager.init()
    }

    private fun getAppVersionName() {
        appVersion = getVersion()
    }

    fun isLatestVersion() : Boolean {

        android.util.Log.e("TEST", "appVersion: $appVersion")
        android.util.Log.e("TEST", "appVersion2: ${fbRemoteConfigManager.getString(FBRemoteConfigManager.AOS_APP_VERSION_NAME)}")

        val fbVersion = fbRemoteConfigManager.getString(FBRemoteConfigManager.AOS_APP_VERSION_NAME)

        if (TextUtils.isEmpty(fbVersion)) {
            return true
        }

        return appVersion == fbRemoteConfigManager.getString(FBRemoteConfigManager.AOS_APP_VERSION_NAME)
    }

    fun setFCMSubscribe() {
        android.util.Log.d("TEST", "FCM 구독")

        /** FCM 구독 설정  */
        FirebaseMessaging.getInstance().subscribeToTopic(ZerothDefine.FCM_SUBSCRIBE_NAME)
    }

    private fun enableStrictMode() {
        // Android/WebView sometimes commit strict mode violations, see e.g.
        // https://github.com/mozilla-mobile/focus-android/issues/660
        if (AppConstants.isReleaseBuild || AppConstants.isBetaBuild) {
            return
        }

        val threadPolicyBuilder = StrictMode.ThreadPolicy.Builder().detectAll()
        val vmPolicyBuilder = StrictMode.VmPolicy.Builder()
            .detectActivityLeaks()
            .detectFileUriExposure()
            .detectLeakedClosableObjects()
            .detectLeakedRegistrationObjects()
            .detectLeakedSqlLiteObjects()

        threadPolicyBuilder.penaltyLog()
        vmPolicyBuilder.penaltyLog()

        StrictMode.setThreadPolicy(threadPolicyBuilder.build())
        StrictMode.setVmPolicy(vmPolicyBuilder.build())
    }

    private fun initializeWebExtensionSupport() {
        WebExtensionSupport.initialize(
            components.engine,
            components.store,
            onNewTabOverride = { _, engineSession, url ->
                components.tabsUseCases.addTab(
                    url = url,
                    selectTab = true,
                    engineSession = engineSession,
                    private = true
                )
            }
        )
    }

    public fun goUrl(url: String) {
        components.tabsUseCases.removeAllTabs()
        components.tabsUseCases.addTab(
            url,
            source = SessionState.Source.Internal.Menu,
            selectTab = true,
            private = true
        )
/*


        val tabId = applicationContext.components.tabsUseCases.addTab(
            url = url,
            source = SessionState.Source.Internal.Menu,
            selectTab = true,
            private = true
        )
        applicationContext.components.appStore.dispatch(AppAction.OpenTab(tabId))

 */
        /*
        components.tabsUseCases.addTab(
            url,
            source = SessionState.Source.Internal.NewTab,
            selectTab = true,
            private = true
        )

         */
    }

    public  fun startAudioMonitor() {
        audioMonitorThread = AudioMonitorThread()
        audioMonitorThread!!.start()
    }
    public  fun stopAudioMonitor() {
        if (audioMonitorThread != null)
        {
            audioMonitorThread!!.stopThread()
            audioMonitorThread = null
        }
    }
}
