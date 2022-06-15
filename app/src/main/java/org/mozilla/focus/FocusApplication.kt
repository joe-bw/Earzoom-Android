/* -*- Mode: Java; c-basic-offset: 4; tab-width: 20; indent-tabs-mode: nil; -*-
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.focus

//import org.mozilla.focus.utils.AdjustHelper
import android.os.StrictMode
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.preference.PreferenceManager
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.sdk.common.KakaoSdk
import kotlinx.coroutines.*
import kr.co.sorizava.asrplayer.AppConfig
import kr.co.sorizava.asrplayer.ZerothDefine
import mozilla.components.browser.state.state.SessionState
import mozilla.components.support.base.facts.register
import mozilla.components.support.base.log.Log
import mozilla.components.support.base.log.sink.AndroidLogSink
import mozilla.components.support.ktx.android.content.isMainProcess
import mozilla.components.support.webextensions.WebExtensionSupport
import org.mozilla.focus.activity.AudioMonitorThread
import org.mozilla.focus.biometrics.LockObserver
import org.mozilla.focus.locale.LocaleAwareApplication
import org.mozilla.focus.login.data.LoginManager
import org.mozilla.focus.navigation.StoreLink
import org.mozilla.focus.session.VisibilityLifeCycleCallback
import org.mozilla.focus.telemetry.FactsProcessor
import org.mozilla.focus.telemetry.ProfilerMarkerFactProcessor
import org.mozilla.focus.telemetry.TelemetryWrapper
import org.mozilla.focus.utils.AppConstants
import kotlin.coroutines.CoroutineContext

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

            // jhong - kakao init
            KakaoSdk.init(this, getString(R.string.kakao_app_key))

            // jhong - facebook init
//        FacebookSdk.sdkInitialize(applicationContext)
//        AppEventsLogger.activateApp(this);

            // jhong - 로그인매니저(토큰 등 관리)
            LoginManager.onInit(this@FocusApplication)

            // jhong - google init
            FirebaseApp.initializeApp(this)

            firebaseAnalytics = Firebase.analytics
        }
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
