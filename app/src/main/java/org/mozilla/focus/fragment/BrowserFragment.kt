/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.focus.fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.*
import android.text.Spannable
import android.text.SpannableString
import android.text.method.MovementMethod
import android.text.method.ScrollingMovementMethod
import android.text.style.BackgroundColorSpan
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.compose.ui.graphics.toArgb
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.preference.PreferenceManager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.browser_display_toolbar.view.*
import kotlinx.android.synthetic.main.fragment_browser.*
import kotlinx.android.synthetic.main.fragment_browser.view.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kr.co.sorizava.asrplayer.AppConfig
import kr.co.sorizava.asrplayer.UIUtils
import kr.co.sorizava.asrplayer.entity.ZerothMessage
import kr.co.sorizava.asrplayer.helper.PlayerCaptionHelper
import kr.co.sorizava.asrplayer.unity.*
import kr.co.sorizava.asrplayer.viewmodel.BrowserFragmentViewModel
import mozilla.components.browser.state.selector.findTabOrCustomTab
import mozilla.components.browser.state.selector.privateTabs
import mozilla.components.browser.state.state.CustomTabConfig
import mozilla.components.browser.state.state.SessionState
import mozilla.components.browser.state.state.content.DownloadState
import mozilla.components.browser.state.state.createTab
import mozilla.components.browser.toolbar.BrowserToolbar
import mozilla.components.concept.engine.EngineView
import mozilla.components.concept.engine.HitResult
import mozilla.components.concept.engine.mediasession.MediaSession
import mozilla.components.feature.app.links.AppLinksFeature
import mozilla.components.feature.contextmenu.ContextMenuCandidate
import mozilla.components.feature.contextmenu.ContextMenuFeature
import mozilla.components.feature.downloads.AbstractFetchDownloadService
import mozilla.components.feature.downloads.DownloadsFeature
import mozilla.components.feature.downloads.manager.FetchDownloadManager
import mozilla.components.feature.downloads.share.ShareDownloadFeature
import mozilla.components.feature.findinpage.view.FindInPageBar
import mozilla.components.feature.prompts.PromptFeature
import mozilla.components.feature.session.SessionFeature
import mozilla.components.feature.tabs.WindowFeature
import mozilla.components.feature.top.sites.TopSitesConfig
import mozilla.components.feature.top.sites.TopSitesFeature
import mozilla.components.lib.crash.Crash
import mozilla.components.support.base.feature.PermissionsFeature
import mozilla.components.support.base.feature.ViewBoundFeatureWrapper
import mozilla.components.support.ktx.kotlin.tryGetHostFromUrl
import org.json.JSONException
import org.json.JSONObject
import org.mozilla.focus.GleanMetrics.TrackingProtection
import org.mozilla.focus.R
import org.mozilla.focus.activity.BookmarksActivity
import org.mozilla.focus.activity.InstallFirefoxActivity
import org.mozilla.focus.activity.MainActivity
import org.mozilla.focus.browser.DisplayToolbar
import org.mozilla.focus.browser.binding.TabCountBinding
import org.mozilla.focus.browser.integration.BrowserMenuController
import org.mozilla.focus.browser.integration.BrowserToolbarIntegration
import org.mozilla.focus.browser.integration.FindInPageIntegration
import org.mozilla.focus.browser.integration.FullScreenIntegration
import org.mozilla.focus.databinding.FragmentBrowserBinding
import org.mozilla.focus.downloads.DownloadService
import org.mozilla.focus.engine.EngineSharedPreferencesListener
import org.mozilla.focus.exceptions.ExceptionDomains
import org.mozilla.focus.ext.*
import org.mozilla.focus.menu.browser.DefaultBrowserMenu
import org.mozilla.focus.open.OpenWithFragment
import org.mozilla.focus.popup.PopupUtils
import org.mozilla.focus.settings.privacy.ConnectionDetailsPanel
import org.mozilla.focus.settings.privacy.TrackingProtectionPanel
import org.mozilla.focus.state.AppAction
import org.mozilla.focus.state.Screen
import org.mozilla.focus.telemetry.TelemetryWrapper
import org.mozilla.focus.topsites.DefaultTopSitesStorage.Companion.TOP_SITES_MAX_LIMIT
import org.mozilla.focus.topsites.DefaultTopSitesView
import org.mozilla.focus.utils.*
import org.mozilla.focus.utils.AppPermissionCodes.REQUEST_CODE_DOWNLOAD_PERMISSIONS
import org.mozilla.focus.utils.AppPermissionCodes.REQUEST_CODE_PROMPT_PERMISSIONS
import org.mozilla.focus.widget.FloatingEraseButton
import org.mozilla.focus.widget.FloatingSessionsButton
import java.lang.ref.WeakReference


/**
 * Fragment for displaying the browser UI.
 */
@Suppress("LargeClass", "TooManyFunctions")
class BrowserFragment :
    BaseFragment(),
    View.OnClickListener {

    private var statusBar: View? = null
    private var urlBar: View? = null
    private var popupTint: FrameLayout? = null

    private var engineView: EngineView? = null

    private val findInPageIntegration = ViewBoundFeatureWrapper<FindInPageIntegration>()
    private val fullScreenIntegration = ViewBoundFeatureWrapper<FullScreenIntegration>()

    private val sessionFeature = ViewBoundFeatureWrapper<SessionFeature>()
    private val promptFeature = ViewBoundFeatureWrapper<PromptFeature>()
    private val contextMenuFeature = ViewBoundFeatureWrapper<ContextMenuFeature>()
    private val downloadsFeature = ViewBoundFeatureWrapper<DownloadsFeature>()
    private val shareDownloadFeature = ViewBoundFeatureWrapper<ShareDownloadFeature>()
    private val windowFeature = ViewBoundFeatureWrapper<WindowFeature>()
    private val appLinksFeature = ViewBoundFeatureWrapper<AppLinksFeature>()
    private val topSitesFeature = ViewBoundFeatureWrapper<TopSitesFeature>()

    private val toolbarIntegration = ViewBoundFeatureWrapper<BrowserToolbarIntegration>()

    private val tabCountBinding = ViewBoundFeatureWrapper<TabCountBinding>()
    private lateinit var trackingProtectionPanel: TrackingProtectionPanel

    private var mSubtitleOnOff: Boolean = false // subtitle on/off
    private var mSubtitlePoistion: Int = 0 // subtitle position
    private var mSubtitleLine: Int = 0 // subtitle line count
    private var mSubtitleFont: Int = 0 // subtitle font size
    private var mSubtitleFontSize: Int = 0 // subtitle font size
    private var mSubtitleForegroundColor: Int = 0
    private var mSubtitleTransparency: Int = 0 // subtitle transparency
    private var mSzSubtitleView: TextView? = null

    private var observedPlaybackState: MediaSession.PlaybackState? = null

    /**
     * The ID of the tab associated with this fragment.
     */
    private val tabId: String
        get() = requireArguments().getString(ARGUMENT_SESSION_UUID)
            ?: throw IllegalAccessError("No session ID set on fragment")

    /**
     * The tab associated with this fragment.
     */
    val tab: SessionState
        get() = requireComponents.store.state.findTabOrCustomTab(tabId)
        // Workaround for tab not existing temporarily.
            ?: createTab("about:blank")

    private lateinit var binding: FragmentBrowserBinding
    private val viewModel : BrowserFragmentViewModel by viewModels()

    @Suppress("LongMethod", "ComplexMethod")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_browser,container,false)
        binding.lifecycleOwner = requireActivity()
        binding.broswerFragmentVM = viewModel

        val view = binding.root

        //val view = inflater.inflate(R.layout.fragment_browser, container, false)
        urlBar = view.urlbar
        statusBar = view.status_bar_background

        popupTint = view.popup_tint

        mSzSubtitleView = view.sz_subtitle_view as TextView
        mSzSubtitleView?.setOnTouchListener (captionTouchListener)



        //this.SetBackgroundColorSpan()
        ////mvvm ?????? ??????
        viewModel.mCSSpeakerText_LiveData.observe(requireActivity()){String ->

            var msg =""
            for( i in viewModel.mCSSpeakerText_LiveData.value!!.indices)
            {
                /*
                Log.e("observefunc()1 :",
                    viewModel.mCSSpeakerText_LiveData.value!![i].mSpeakerNum.toString() +
                            viewModel.mCSSpeakerText_LiveData.value!![i].mText.toString()
                )
                */
                msg = msg + viewModel.mCSSpeakerText_LiveData.value!![i].mText.toString()

                //Log.e("observefunc()2 :", viewModel.mCSSpeakerText_LiveData.value!![i].mSpeakerNum.toString())
                //Log.e("observefunc()3 :", viewModel.mCSSpeakerText_LiveData.value!![i].mText.toString())
                //Log.e("observefunc()4 msg:",msg)
            }
            this.SubtitleTextOutBySpeakerNum(true,msg!!)
            //this.SubtitleTextOutBySpeakerNum(false,msg!!)
            Log.e("observefunc()","aaaaaaaaaaa")
            if( msg =="")
            {
                Log.e("observefunc() msg:",msg)
            }
            else
            {


                if( AppliactionUnity.getInstance()!!.getUnityView()!!.visibility == View.INVISIBLE ||
                    AppliactionUnity.getInstance()!!.getUnityView()!!.visibility == View.GONE ) {

                    AppliactionUnity.getInstance()!!.getUnityView()!!.visibility = View.VISIBLE
                    AppliactionUnity.getInstance()!!.getUnityView()!!.setTranslationY(  "0".toFloat() )
                    resetUnityViewPositon()
/*
                if( tempFrameLayout?.visibility == View.INVISIBLE ||
                    tempFrameLayout?.visibility == View.GONE ) {
                    tempFrameLayout?.visibility = View.VISIBLE
*/
                /*
                if( AppliactionUnity.unityView!!.visibility == View.INVISIBLE ||
                    AppliactionUnity.unityView!!.visibility == View.GONE ) {
                    AppliactionUnity.unityView!!.visibility = View.VISIBLE
*/
/*

                if( mUnityView!!.visibility == View.INVISIBLE ||
                    mUnityView!!.visibility == View.GONE ) {
                    mUnityView!!.visibility = View.VISIBLE
*/

                val `object` = JSONObject()

                try {
                    `object`.put("id", "signlanguage_final")
                    `object`.put("time", 0.2)
                    `object`.put("text", "??????+?????????")
                    `object`.put("origin_text", "??????????????????")
                } catch (e: JSONException) {
                    Log.e("NewsListFragment", "error parse json", e)
                } finally {
                    UnityBridgeManager.getInstance()
                        .newUnitySendMessage("SetRecieveLyrics", `object`.toString())
                    UnityBridgeManager.getInstance()
                        .newUnitySendMessage("SetRecieveLyrics", `object`.toString())
                }

                }
            }
        }

        //viewModel.mCSSpeakerText_LiveData.observe(requireActivity(), ::observefunc)
        //viewModel.getSpeakerText().observe(requireActivity(), ::observefunc)



        //observe(viewModel.mNowCSSpeakerText_LiveData, this::observefunc1)
/*
        //val view = inflater.inflate(R.layout.fragment_browser, container, false)
        urlBar = view.findViewById(R.id.urlbar)
        statusBar = view.findViewById(R.id.status_bar_background)

        popupTint = view.findViewById(R.id.popup_tint)

        mSzSubtitleView = view.findViewById(R.id.sz_subtitle_view) as TextView
        mSzSubtitleView?.setOnTouchListener (captionTouchListener)
*/

        //mSzSubtitleView?.setOnTouchListener (captionTouchListener)
        return view
    }

    //2020707 cbw unity ??????
    //private  var mUnityView : UnityView? = null

    fun InitUnityView()
    {
        /*
        //1. ?????? ?????? ????????? ?????? xml ??????
        requestedOrientation =
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                //setContentView(R.layout.activity_portrait);
                setContentView(R.layout.unity_main_port)
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            } else {
                //setContentView(R.layout.activity_landscape);
                setContentView(R.layout.unity_main)
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
        */
        //2. unity ??? ??????
//        unityView =  binding.customUnityView



        //tempFrameLayout?.visibility = View.INVISIBLE

        //unityViewFrameLayout  = activity?.findViewById<RelativeLayout>(R.id.custom_unity_view_FrameLayout)!!
        //unityViewFrameLayout?.visibility = View.VISIBLE

        if( AppliactionUnity.getInstance()!!.getUnityView() == null) {
            AppliactionUnity.getInstance()!!.setUnityView(requireActivity())

            AppliactionUnity.getInstance()!!.getUnityView()?.start() //(ContextWrapper) GlobalApplication.getInstance());
            //?????? ???????????? ????????????
            AppliactionUnity.getInstance()!!.getUnityView()?.resetOverlay()

        }

    }




    @Suppress("ComplexCondition", "LongMethod")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val components = requireComponents

        engineView = (view.findViewById<View>(R.id.webview) as EngineView)

        val toolbarView = view.findViewById<DisplayToolbar>(R.id.appbar)

        findInPageIntegration.set(
            FindInPageIntegration(
                components.store,
                view.findViewById(R.id.find_in_page),
                engineView!!
            ),
            this, view
        )

        fullScreenIntegration.set(
            FullScreenIntegration(
                requireActivity(),
                components.store,
                tab.id,
                components.sessionUseCases,
                toolbarView!!,
                statusBar!!
            ),
            this, view
        )

        contextMenuFeature.set(
            ContextMenuFeature(
                parentFragmentManager,
                components.store,
                ContextMenuCandidate.defaultCandidates(
                    requireContext(),
                    components.tabsUseCases,
                    components.contextMenuUseCases,
                    view
                ) +
                        ContextMenuCandidate.createOpenInExternalAppCandidate(
                            requireContext(),
                            components.appLinksUseCases
                        ),
                engineView!!,
                requireComponents.contextMenuUseCases,
                tabId,
                additionalNote = { hitResult -> getAdditionalNote(hitResult) }
            ),
            this, view
        )

        sessionFeature.set(
            SessionFeature(
                components.store,
                components.sessionUseCases.goBack,
                engineView!!,
                tab.id
            ),
            this, view
        )

        promptFeature.set(
            PromptFeature(
                fragment = this,
                store = components.store,
                customTabId = tab.id,
                fragmentManager = parentFragmentManager,
                onNeedToRequestPermissions = { permissions ->
                    @Suppress("DEPRECATION") // https://github.com/mozilla-mobile/focus-android/issues/4959
                    requestPermissions(permissions, REQUEST_CODE_PROMPT_PERMISSIONS)
                }
            ),
            this, view
        )

        downloadsFeature.set(
            DownloadsFeature(
                requireContext().applicationContext,
                components.store,
                components.downloadsUseCases,
                fragmentManager = childFragmentManager,
                downloadManager = FetchDownloadManager(
                    requireContext().applicationContext,
                    components.store,
                    DownloadService::class
                ),
                onNeedToRequestPermissions = { permissions ->
                    @Suppress("DEPRECATION") // https://github.com/mozilla-mobile/focus-android/issues/4959
                    requestPermissions(permissions, REQUEST_CODE_DOWNLOAD_PERMISSIONS)
                },
                onDownloadStopped = { state, _, status ->
                    showDownloadSnackbar(state, status)
                }
            ),
            this, view
        )

        shareDownloadFeature.set(
            ShareDownloadFeature(
                context = requireContext().applicationContext,
                httpClient = components.client,
                store = components.store,
                tabId = tab.id
            ),
            this, view
        )

        appLinksFeature.set(
            feature = AppLinksFeature(
                requireContext(),
                store = components.store,
                sessionId = tabId,
                fragmentManager = parentFragmentManager,
                launchInApp = { true },
                loadUrlUseCase = requireContext().components.sessionUseCases.loadUrl
            ),
            owner = this,
            view = view
        )

        topSitesFeature.set(
            feature = TopSitesFeature(
                view = DefaultTopSitesView(requireComponents.appStore),
                storage = requireComponents.topSitesStorage,
                config = {
                    TopSitesConfig(
                        totalSites = TOP_SITES_MAX_LIMIT,
                        frecencyConfig = null
                    )
                }
            ),
            owner = this,
            view = view
        )

        customizeToolbar(view)
        customizeFindInPage(view)

        val customTabConfig = tab.ifCustomTab()?.config
        if (customTabConfig != null) {
            initialiseCustomTabUi(view, customTabConfig)

            // TODO Add custom tabs window feature support
            // We to add support for Custom Tabs here, however in order to send the window request
            // back to us through the intent system, we need to register a unique schema that we
            // can handle. For example, Fenix Nighlyt does this today with `fenix-nightly://`.
        } else {
            initialiseNormalBrowserUi(view)

            windowFeature.set(
                feature = WindowFeature(
                    store = components.store,
                    tabsUseCases = components.tabsUseCases
                ),
                owner = this,
                view = view
            )
        }

        //2020707 cbw unity ??????
        this.InitUnityView()
    }

    private fun getAdditionalNote(hitResult: HitResult): String? {
        return if ((hitResult is HitResult.IMAGE_SRC || hitResult is HitResult.IMAGE) &&
            hitResult.src.isNotEmpty()
        ) {
            getString(R.string.contextmenu_erased_images_note2, getString(R.string.app_name))
        } else {
            null
        }
    }

    private fun customizeFindInPage(view: View) {
        val findInPageBar = view.findViewById<FindInPageBar>(R.id.find_in_page)
        val newParams = findInPageBar.layoutParams as CoordinatorLayout.LayoutParams
        newParams.gravity = Gravity.BOTTOM
        findInPageBar.layoutParams = newParams
    }

    private fun customizeToolbar(view: View) {
        val browserToolbar = view.findViewById<BrowserToolbar>(R.id.browserToolbar)
        val controller = BrowserMenuController(
            requireComponents.sessionUseCases,
            requireComponents.appStore,
            requireComponents.store,
            requireComponents.topSitesUseCases,
            tabId,
            ::shareCurrentUrl,
            ::setShouldRequestDesktop,
            ::showAddToHomescreenDialog,
            ::showFindInPageBar,
            ::openSelectBrowser,
            ::openInBrowser,
            ::openBookmarks
        )

        if (tab.ifCustomTab()?.config == null) {
            val browserMenu = DefaultBrowserMenu(
                context = requireContext(),
                appStore = requireComponents.appStore,
                store = requireComponents.store,
                isPinningSupported = ShortcutManagerCompat.isRequestPinShortcutSupported(
                    requireContext()
                ),
                onItemTapped = { controller.handleMenuInteraction(it) }
            )
            browserToolbar.display.menuBuilder = browserMenu.menuBuilder
        }

        toolbarIntegration.set(
            BrowserToolbarIntegration(
                requireComponents.store,
                browserToolbar,
                fragment = this,
                controller = controller,
                customTabId = if (tab.isCustomTab()) {
                    tab.id
                } else {
                    null
                },
                customTabsUseCases = requireComponents.customTabsUseCases,
                sessionUseCases = requireComponents.sessionUseCases,
                onUrlLongClicked = ::onUrlLongClicked,
                onCheckStartURL = ::onCheckStartURL
            ),
            owner = this,
            view = browserToolbar
        )
    }

    private fun initialiseNormalBrowserUi(view: View) {
        val eraseButton = view.findViewById<FloatingEraseButton>(R.id.erase)
        eraseButton.setOnClickListener(this)

        val tabsButton = view.findViewById<FloatingSessionsButton>(R.id.tabs)
        tabsButton.setOnClickListener(this)

        tabCountBinding.set(
            TabCountBinding(
                requireComponents.store,
                eraseButton,
                tabsButton
            ),
            owner = this,
            view = eraseButton
        )
    }

    private fun initialiseCustomTabUi(view: View, customTabConfig: CustomTabConfig) {
        // Unfortunately there's no simpler way to have the FAB only in normal-browser mode.
        // - ViewStub: requires splitting attributes for the FAB between the ViewStub, and actual FAB layout file.
        //             Moreover, the layout behaviour just doesn't work unless you set it programatically.
        // - View.GONE: doesn't work because the layout-behaviour makes the FAB visible again when scrolling.
        // - Adding at runtime: works, but then we need to use a separate layout file (and you need
        //   to set some attributes programatically, same as ViewStub).
        val erase = view.findViewById<FloatingEraseButton>(R.id.erase)
        val eraseContainer = erase.parent as ViewGroup
        eraseContainer.removeView(erase)

        val sessions = view.findViewById<FloatingSessionsButton>(R.id.tabs)
        eraseContainer.removeView(sessions)

        if (!customTabConfig.enableUrlbarHiding) {
            val params = urlBar!!.layoutParams as AppBarLayout.LayoutParams
            params.scrollFlags = 0
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // This fragment might get destroyed before the user left immersive mode (e.g. by opening another URL from an
        // app). In this case let's leave immersive mode now when the fragment gets destroyed.
        fullScreenIntegration.get()?.exitImmersiveModeIfNeeded()

/*
        //AppliactionUnity.unityView!!.RemoveUnityView()
        mUnityView!!.destroy()
        mUnityView = null
        */
        AppliactionUnity.getInstance()!!.getUnityView()!!.destroy()
        AppliactionUnity.getInstance()!!.setUnityView(null)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        val feature: PermissionsFeature? = when (requestCode) {
            REQUEST_CODE_PROMPT_PERMISSIONS -> promptFeature.get()
            REQUEST_CODE_DOWNLOAD_PERMISSIONS -> downloadsFeature.get()
            else -> null
        }

        feature?.onPermissionsResult(permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        promptFeature.withFeature { it.onActivityResult(requestCode, data, resultCode) }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun showCrashReporter(crash: Crash) {
        val fragmentManager = requireActivity().supportFragmentManager

        if (crashReporterIsVisible()) {
            // We are already displaying the crash reporter
            // No need to show another one.
            return
        }

        val crashReporterFragment = CrashReporterFragment.create()

        crashReporterFragment.onCloseTabPressed = { sendCrashReport ->
            if (sendCrashReport) {
                val crashReporter = requireComponents.crashReporter

                GlobalScope.launch(Dispatchers.IO) { crashReporter.submitReport(crash) }
            }
            erase()
            hideCrashReporter()
        }

        fragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .add(R.id.crash_container, crashReporterFragment, CrashReporterFragment.FRAGMENT_TAG)
            .commit()

        crash_container.visibility = View.VISIBLE
        tabs.hide()
        erase.hide()
    }

    private fun hideCrashReporter() {
        val fragmentManager = requireActivity().supportFragmentManager
        val fragment = fragmentManager.findFragmentByTag(CrashReporterFragment.FRAGMENT_TAG)
            ?: return

        fragmentManager
            .beginTransaction()
            .remove(fragment)
            .commit()

        crash_container.visibility = View.GONE
        tabs.show()
        erase.show()
    }

    fun crashReporterIsVisible(): Boolean = requireActivity().supportFragmentManager.let {
        it.findFragmentByTag(CrashReporterFragment.FRAGMENT_TAG)?.isVisible ?: false
    }

    private fun showDownloadSnackbar(
        state: DownloadState,
        status: DownloadState.Status,
    ) {
        if (status != DownloadState.Status.COMPLETED) {
            // We currently only show an in-app snackbar for completed downloads.
            return
        }

        val snackbar = Snackbar.make(
            requireView(),
            String.format(
                requireContext().getString(R.string.download_snackbar_finished),
                state.fileName
            ),
            Snackbar.LENGTH_LONG
        )

        snackbar.setAction(getString(R.string.download_snackbar_open)) {
            val opened = AbstractFetchDownloadService.openFile(
                applicationContext = requireContext().applicationContext,
                download = state
            )

            if (!opened) {
                val extension = MimeTypeMap.getFileExtensionFromUrl(state.filePath)

                Toast.makeText(
                    context,
                    getString(
                        R.string.mozac_feature_downloads_open_not_supported1,
                        extension
                    ),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        snackbar.setActionTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.snackbarActionText
            )
        )

        snackbar.show()
    }

    private fun showAddToHomescreenDialog() {
        val fragmentManager = childFragmentManager

        if (fragmentManager.findFragmentByTag(AddToHomescreenDialogFragment.FRAGMENT_TAG) != null) {
            // We are already displaying a homescreen dialog fragment (Probably a restored fragment).
            // No need to show another one.
            return
        }

        val requestDesktop = tab.content.desktopMode

        val addToHomescreenDialogFragment = AddToHomescreenDialogFragment.newInstance(
            tab.content.url,
            tab.content.titleOrDomain,
            tab.trackingProtection.enabled,
            requestDesktop = requestDesktop
        )

        try {
            addToHomescreenDialogFragment.show(
                fragmentManager,
                AddToHomescreenDialogFragment.FRAGMENT_TAG
            )
        } catch (e: IllegalStateException) {
            // It can happen that at this point in time the activity is already in the background
            // and onSaveInstanceState() has already been called. Fragment transactions are not
            // allowed after that anymore. It's probably safe to guess that the user might not
            // be interested in adding to homescreen now.
        }
    }

    override fun onResume() {
        super.onResume()


        AppliactionUnity.getInstance()!!.getUnityView()!!.resume()
        //AppliactionUnity.unityView!!.visibility = View.INVISIBLE

        setupSubtitleView()

        resetSubtitlePositon()
        resetUnityViewPositon()
        AppliactionUnity.getInstance()!!.getUnityView()!!.setTranslationY(  "-10000.0".toFloat() )
        AppliactionUnity.getInstance()!!.getUnityView()!!.visibility = View.INVISIBLE

        //AppliactionUnity.unityView!!.setTranslationY(  "-10000.0".toFloat() )
        //AppliactionUnity.unityView!!.visibility = View.INVISIBLE

        StatusBarUtils.getStatusBarHeight(statusBar) { statusBarHeight ->
            statusBar!!.layoutParams.height = statusBarHeight
        }


    }

    @Suppress("ComplexMethod", "ReturnCount")
    fun onBackPressed(): Boolean {
        if (findInPageIntegration.onBackPressed()) {
            return true
        } else if (fullScreenIntegration.onBackPressed()) {
            return true
        } else if (sessionFeature.get()?.onBackPressed() == true) {
            return true
        } else {
            if (tab.source is SessionState.Source.External || tab.isCustomTab()) {
                TelemetryWrapper.eraseBackToAppEvent()

                // This session has been started from a VIEW intent. Go back to the previous app
                // immediately and erase the current browsing session.
                erase()

                // If there are no other sessions then we remove the whole task because otherwise
                // the old session might still be partially visible in the app switcher.
                if (requireComponents.store.state.privateTabs.isEmpty()) {
                    requireActivity().finishAndRemoveTask()
                } else {
                    requireActivity().finish()
                }
                // We can't show a snackbar outside of the app. So let's show a toast instead.
                Toast.makeText(context, R.string.feedback_erase_custom_tab, Toast.LENGTH_SHORT)
                    .show()
            } else {
                // Just go back to the home screen.
                TelemetryWrapper.eraseBackToHomeEvent()

                erase()
            }
        }

        return true
    }

    fun erase() {
        val context = context

        // Notify the user their session has been erased if Talk Back is enabled:
        if (context != null) {
            val manager =
                context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
            if (manager.isEnabled) {
                val event = AccessibilityEvent.obtain()
                event.eventType = AccessibilityEvent.TYPE_ANNOUNCEMENT
                event.className = javaClass.name
                event.packageName = requireContext().packageName
                event.text.add(getString(R.string.feedback_erase2))
            }
        }

        requireComponents.tabsUseCases.removeTab(tab.id)
    }

    private fun shareCurrentUrl() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, tab.content.url)

        val title = tab.content.title
        if (title.isNotEmpty()) {
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, title)
        }

        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_dialog_title)))

        TelemetryWrapper.shareEvent()
    }

    private fun openBookmarks() {
        startActivity(Intent(context, BookmarksActivity::class.java))
    }

    private fun openInBrowser() {
        // Release the session from this view so that it can immediately be rendered by a different view
        sessionFeature.get()?.release()

        requireComponents.customTabsUseCases.migrate(tab.id)

        val intent = Intent(context, MainActivity::class.java)
        intent.action = Intent.ACTION_MAIN
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)

        TelemetryWrapper.openFullBrowser()

        val activity = activity
        activity?.finish()
    }

    internal fun edit() {
        requireComponents.appStore.dispatch(
            AppAction.EditAction(tab.id)
        )
    }

    @Suppress("ComplexMethod")
    override fun onClick(view: View) {
        when (view.id) {
            R.id.erase -> {
                TelemetryWrapper.eraseEvent()

                erase()
            }

            R.id.tabs -> {
                requireComponents.appStore.dispatch(AppAction.ShowTabs)

                TelemetryWrapper.openTabsTrayEvent()
            }

            R.id.open_in_firefox_focus -> {
                openInBrowser()
            }

            R.id.share -> {
                shareCurrentUrl()
            }

            // add: sorizava bookmark
            R.id.bookmarks -> {
                openBookmarks()
            }

            R.id.settings -> {
                requireComponents.appStore.dispatch(
                    AppAction.OpenSettings(page = Screen.Settings.Page.Start)
                )
            }

            R.id.open_default -> {
                val browsers = Browsers(requireContext(), tab.content.url)

                val defaultBrowser = browsers.defaultBrowser
                    ?: throw IllegalStateException("<Open with \$Default> was shown when no default browser is set")
                // We only add this menu item when a third party default exists, in
                // BrowserMenuAdapter.initializeMenu()

                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(tab.content.url))
                intent.setPackage(defaultBrowser.packageName)
                startActivity(intent)

                if (browsers.isFirefoxDefaultBrowser) {
                    TelemetryWrapper.openFirefoxEvent()
                } else {
                    TelemetryWrapper.openDefaultAppEvent()
                }
            }

            R.id.open_select_browser -> {
                openSelectBrowser()
            }

            R.id.help -> {
                requireComponents.tabsUseCases.addTab(
                    SupportUtils.HELP_URL,
                    source = SessionState.Source.Internal.Menu,
                    selectTab = true,
                    private = true
                )
            }

            R.id.stop -> {
                requireComponents.sessionUseCases.stopLoading(tabId)
            }

            R.id.refresh -> {
                requireComponents.sessionUseCases.reload(tabId)
            }

            R.id.forward -> {
                requireComponents.sessionUseCases.goForward(tabId)
            }

            R.id.help_trackers -> {
                val url = SupportUtils.getSumoURLForTopic(
                    requireContext(),
                    SupportUtils.SumoTopic.TRACKERS
                )
                requireComponents.tabsUseCases.addTab(
                    url,
                    source = SessionState.Source.Internal.Menu,
                    selectTab = true,
                    private = true
                )
            }

            R.id.add_to_homescreen -> {
                showAddToHomescreenDialog()
            }

            R.id.report_site_issue -> {
                val reportUrl = String.format(SupportUtils.REPORT_SITE_ISSUE_URL, tab.content.url)
                requireComponents.tabsUseCases.addTab(
                    reportUrl,
                    source = SessionState.Source.Internal.Menu,
                    selectTab = true,
                    private = true
                )

                TelemetryWrapper.reportSiteIssueEvent()
            }

            R.id.find_in_page -> {
                showFindInPageBar()
            }

            else -> throw IllegalArgumentException("Unhandled menu item in BrowserFragment")
        }
    }

    private fun showFindInPageBar() {
        findInPageIntegration.get()?.show(tab)
        TelemetryWrapper.findInPageMenuEvent()
    }

    private fun openSelectBrowser() {

        val browsers = Browsers(requireContext(), tab.content.url)

        val apps = browsers.installedBrowsers
        val store = if (browsers.hasFirefoxBrandedBrowserInstalled())
            null
        else
            InstallFirefoxActivity.resolveAppStore(requireContext())

        val fragment = OpenWithFragment.newInstance(
            apps,
            tab.content.url,
            store
        )
        @Suppress("DEPRECATION")
        fragment.show(requireFragmentManager(), OpenWithFragment.FRAGMENT_TAG)

        TelemetryWrapper.openSelectionEvent()
    }

    internal fun closeCustomTab() {
        TelemetryWrapper.closeCustomTabEvent()

        requireComponents.customTabsUseCases.remove(tab.id)

        requireActivity().finish()

        TelemetryWrapper.closeCustomTabEvent()
    }

    fun setShouldRequestDesktop(enabled: Boolean) {
        if (enabled) {
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putBoolean(
                    requireContext().getString(R.string.has_requested_desktop),
                    true
                ).apply()
        }
        TelemetryWrapper.desktopRequestCheckEvent(enabled)
        requireComponents.sessionUseCases.requestDesktopSite(enabled, tab.id)
    }

    fun showSecurityPopUp() {
        if (crashReporterIsVisible()) {
            return
        }

        // Don't show Security Popup if the page is loading
        if (tab.content.loading) {
            return
        }
        val securityPopup = PopupUtils.createSecurityPopup(requireContext(), tab)
        if (securityPopup != null) {
            securityPopup.setOnDismissListener { popupTint!!.visibility = View.GONE }
            securityPopup.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            securityPopup.animationStyle = android.R.style.Animation_Dialog
            securityPopup.isTouchable = true
            securityPopup.isFocusable = true
            securityPopup.elevation = resources.getDimension(R.dimen.menu_elevation)
            val offsetY =
                requireContext().resources.getDimensionPixelOffset(R.dimen.doorhanger_offsetY)
            securityPopup.showAtLocation(
                urlBar,
                Gravity.TOP or Gravity.CENTER_HORIZONTAL,
                0,
                offsetY
            )
            popupTint!!.visibility = View.VISIBLE
        }
    }

    fun showTrackingProtectionPanel() {
        trackingProtectionPanel = TrackingProtectionPanel(
            context = requireContext(),
            tabUrl = tab.content.url,
            isTrackingProtectionOn = tab.trackingProtection.ignoredOnTrackingProtection.not(),
            isConnectionSecure = tab.content.securityInfo.secure,
            blockedTrackersCount = Settings.getInstance(requireContext())
                .getTotalBlockedTrackersCount(),
            toggleTrackingProtection = ::toggleTrackingProtection,
            updateTrackingProtectionPolicy = { tracker, isEnabled ->
                EngineSharedPreferencesListener(requireContext())
                    .updateTrackingProtectionPolicy(
                        source = EngineSharedPreferencesListener.ChangeSource.PANEL.source,
                        tracker = tracker,
                        isEnabled = isEnabled
                    )
            },
            showConnectionInfo = ::showConnectionInfo
        )
        trackingProtectionPanel.show()
    }

    private fun showConnectionInfo() {
        val connectionInfoPanel = ConnectionDetailsPanel(
            context = requireContext(),
            tabTitle = tab.content.title,
            tabUrl = tab.content.url,
            isConnectionSecure = tab.content.securityInfo.secure,
            goBack = { trackingProtectionPanel.show() }
        )
        trackingProtectionPanel.hide()
        connectionInfoPanel.show()
    }

    private fun toggleTrackingProtection(enable: Boolean) {
        val context = requireContext()
        with(requireComponents) {
            if (enable) {
                ExceptionDomains.remove(context, listOf(tab.content.url.tryGetHostFromUrl()))
                trackingProtectionUseCases.removeException(tab.id)
            } else {
                ExceptionDomains.add(context, tab.content.url.tryGetHostFromUrl())
                trackingProtectionUseCases.addException(tab.id)
            }
            sessionUseCases.reload(tab.id)
        }

        TrackingProtection.hasEverChangedEtp.set(true)
        TrackingProtection.trackingProtectionChanged.record(
            TrackingProtection.TrackingProtectionChangedExtra(
                isEnabled = enable
            )
        )
    }

    fun onCallEndTime() {
        Log.d("TEST", "onCheckPrevTab: " + requireComponents.store.state.tabs.size)

        (activity as MainActivity?)?.callEndTime()

    }

    private fun onCheckStartURL() {
        Log.d("TEST", "onCheckStartURL")

        try {
            (activity as MainActivity?)?.checkStartURL(tab.content.url)
        } catch (e: Exception) {
        }
    }

    private fun onUrlLongClicked(): Boolean {
        val context = activity ?: return false

        return if (tab.isCustomTab()) {
            val clipBoard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val uri = Uri.parse(tab.content.url)
            clipBoard.setPrimaryClip(ClipData.newRawUri("Uri", uri))
            Toast.makeText(
                context,
                getString(R.string.custom_tab_copy_url_action),
                Toast.LENGTH_SHORT
            ).show()
            true
        } else {
            false
        }
    }

    fun handleTabCrash(crash: Crash) {
        showCrashReporter(crash)
    }

    companion object {
        const val FRAGMENT_TAG = "browser"

        private const val ARGUMENT_SESSION_UUID = "sessionUUID"

        fun createForTab(tabId: String): BrowserFragment {
            val fragment = BrowserFragment()
            fragment.arguments = Bundle().apply {
                putString(ARGUMENT_SESSION_UUID, tabId)
            }
            return fragment
        }

        private const val TAG = "BrowserFragment"

        private const val MSG_SUBTILTILE_CUE_RESET = 100
    }
/*
    //?????? ?????? ????????? ?????????.
    var mBackgroundColorSpanBySpeakerNumList : MutableList<BackgroundColorSpan> = mutableListOf<BackgroundColorSpan>()

    fun SetBackgroundColorSpan()
    {
        for(i in viewModel.mSpeakerColorList!!.indices)
        {
            mBackgroundColorSpanBySpeakerNumList.add(
                BackgroundColorSpan(
                    PlayerCaptionHelper.getColorWithAlpha(
                        //viewModel.mSpeakerColorList!!.get(0). ,
                        viewModel.mSpeakerColorList!![i].toArgb() ,
                        AppConfig.getInstance()?.convertPrefSubtitleTransparency(mSubtitleTransparency)!!
                    )
                ))
        }
    }
*/

    fun SubtitleTextOutBySpeakerNum(isApplyResetTimer: Boolean,msg: String?){
        // Log.d(LOGTAG, type + " cueTextOut: " +msg);
        try {

            if (isApplyResetTimer) {
                mResetSubtitleHandler.removeMessages(MSG_SUBTILTILE_CUE_RESET)
                mResetSubtitleHandler.sendEmptyMessageDelayed(MSG_SUBTILTILE_CUE_RESET, 5000)
                //mResetSubtitleHandler.sendEmptyMessageDelayed(MSG_SUBTILTILE_CUE_RESET, 5000)
            }
            if (getActivity() != null && msg == null) {
                requireActivity().runOnUiThread(Runnable { mSzSubtitleView!!.text = "" })
            }
            if( viewModel.mCSSpeakerText_LiveData.value == null) return;

/*
            for( i in viewModel.mCSSpeakerText_LiveData.value!!.indices)
            {
                Log.e("observefunc()",
                    viewModel.mCSSpeakerText_LiveData.value!![i].mSpeakerNum.toString() +
                            viewModel.mCSSpeakerText_LiveData.value!![i].mText.toString()
                )
            }
*/

            val spannable: Spannable = SpannableString(msg)
            var textStartIndex =0
            for( i in viewModel.mCSSpeakerText_LiveData.value!!.indices)
            {
                Log.e(TAG, "i:" + i)
                var mText = viewModel.mCSSpeakerText_LiveData.value!![i].mText
                Log.e(TAG, "mText:" + mText)
                var mSpeakerNum = viewModel.mCSSpeakerText_LiveData.value!![i].mSpeakerNum
                Log.e(TAG, "mSpeakerNum:" + mSpeakerNum)
                //Log.e(TAG, "mBackgroundColorSpanBySpeakerNumList[mSpeakerNum]:" + mBackgroundColorSpanBySpeakerNumList[mSpeakerNum])
                Log.e(TAG, "textStartIndex1:" + textStartIndex)
                Log.e(TAG, "mText!!.length:" + mText!!.length)
                spannable.setSpan(
                    //mBackgroundColorSpanBySpeakerNumList[mSpeakerNum],
                    BackgroundColorSpan(
                        PlayerCaptionHelper.getColorWithAlpha(
                            //viewModel.mSpeakerColorList!!.get(0). ,
                            viewModel.mSpeakerColorList!![mSpeakerNum].toArgb() ,
                            AppConfig.getInstance()?.convertPrefSubtitleTransparency(mSubtitleTransparency)!!
                        )
                    ),
                    textStartIndex,
                    textStartIndex + mText!!.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                textStartIndex = textStartIndex + mText!!.length
                Log.e(TAG, "textStartIndex2:" + textStartIndex)
            }

            viewModel.mSpannable_MutableLiveData.value = spannable
            /*
            if (getActivity() != null) {
                requireActivity().runOnUiThread(Runnable { mSzSubtitleView!!.text = spannable })
            }
            */
        }
        catch (ex : Exception)
        {
            Log.e("SubtitleTextOutBySpeakerNum(isApplyResetTimer: Boolean,msg: String?) ex:" ,
                "isApplyResetTimer:" + isApplyResetTimer +",msg:" + msg
             + "," + ex.toString())
        }
    }



    private var mPartialText = ""
    private var mPrevSubtitleText = ""

    var mSpeakerNum :Int = 0
    private var mCsSpeakerText : MutableList<csSpeakerText>?  = mutableListOf<csSpeakerText>()

    fun onMessageSubtitle(message: String) {

        Log.e(TAG, "DPDPDP message:" + message)

            //????????? speaker
        val speakerNumStr : String = "\"speakerNum\":" + ((Math.random()*10) % 2).toInt() + ",";
        var messageWithSpeakerNumStr =message[0] + speakerNumStr + message.substring(1, (message.length) )
        //messageWithSpeakerNumStr = "{\"bayes-risk\": 0.0, \"result\": {\"final\": true, \"hypotheses\": [{\"likelihood\": 0.0, \"transcript\": \"????????? ????????? ???????????????. ?????? ??? ?????? ????????? ??? ??????????????? ?????? ??? ????????? ?????? ???????????? ???????????? ????????? ????????? ?????????. ????????? ?????? ?????? ?????????, ????????? ?????? ????????? ??????????????????. ????????? ????????? ???????????????.\", \"word-alignment\": [{\"confidence\": -1.24, \"length\": 0.48, \"start\": 0.9, \"word\": \"|?????????\"}, {\"confidence\": -2.36, \"length\": 0.32, \"start\": 1.38, \"word\": \"|?????????\"}, {\"confidence\": -4.18, \"length\": 1.16, \"start\": 1.7, \"word\": \"|???????????????.\"}, {\"confidence\": -1.96, \"length\": 0.24, \"start\": 2.86, \"word\": \"|??????\"}, {\"confidence\": -1.73, \"length\": 0.28, \"start\": 3.1, \"word\": \"|???\"}, {\"confidence\": -3.28, \"length\": 0.4, \"start\": 3.38, \"word\": \"|??????\"}, {\"confidence\": -4.22, \"length\": 0.56, \"start\": 3.78, \"word\": \"|?????????\"}, {\"confidence\": -2.32, \"length\": 0.2, \"start\": 4.34, \"word\": \"|???\"}, {\"confidence\": -9.72, \"length\": 0.8, \"start\": 4.54, \"word\": \"|???????????????\"}, {\"confidence\": -4.67, \"length\": 0.24, \"start\": 5.34, \"word\": \"|??????\"}, {\"confidence\": -4.07, \"length\": 0.16, \"start\": 5.58, \"word\": \"|???\"}, {\"confidence\": -0.35, \"length\": 0.4, \"start\": 5.89, \"word\": \"|?????????\"}, {\"confidence\": -0.66, \"length\": 0.2, \"start\": 6.29, \"word\": \"|???        ???\"}, {\"confidence\": -2.15, \"length\": 0.48, \"start\": 6.49, \"word\": \"|????????????\"}, {\"confidence\": -3.71, \"length\": 0.52, \"start\": 6.97, \"word\": \"|????????????\"}, {\"confidence\": -5.46, \"length\": 0.48, \"start\": 7.49, \"word\": \"|>?????????\"}, {\"confidence\": -6.37, \"length\": 0.36, \"start\": 7.97, \"word\": \"|?????????\"}, {\"confidence\": -6.92, \"length\": 1.16, \"start\": 8.33, \"word\": \"|?????????.\"}, {\"confidence\": -7.31, \"length\": 0.56, \"start\": 9.49, \"word\": \"|?????????\"}, {\"confidence\": -6.47, \"length\": 0.2, \"start\": 10.05, \"word\": \"|??????\"}, {\"confidence\": -2.41, \"length\": 0.28, \"start\": 10.4, \"word\": \"|??????\"}, {\"confidence\": -5.33, \"length\": 0.56, \"start\": 10.68, \"word\": \"|??????        ???,\"}, {\"confidence\": -5.76, \"length\": 0.52, \"start\": 11.24, \"word\": \"|?????????\"}, {\"confidence\": -5.78, \"length\": 0.4, \"start\": 11.76, \"word\": \"|??????\"}, {\"confidence\": -7.15, \"length\": 0.36, \"start\": 12.16, \"word\": \"|??????        ???\"}, {\"confidence\": -12.26, \"length\": 1.0, \"start\": 12.52, \"word\": \"|??????????????????.\"}, {\"confidence\": -10.04, \"length\": 0.48, \"start\": 13.52, \"word\": \"|?????????\"}, {\"confidence\": -12.0, \"length\": 0.36, \"start\": 14.0, \"word\": \"|?????????\"}, {\"confidence\": -18.64, \"length\": 0.4, \"start\": 14.36, \"word\": \"|???????????????.\"}]}]}, \"segment\": 0, \"segment-length\": 13.56, \"segment-start\": 0.9, \"speaker\": 0, \"status\": 0, \"total-length\": 14.84}"
        //messageWithSpeakerNumStr = "{\"bayes-risk\": 0.0, \"result\": {\"final\": true, \"hypotheses\": [{\"likelihood\": 0.0, \"transcript\": \"????????? ?????? ?????? ?????????, ????????? ?????? ????????? ??????????????????.\", \"word-alignment\": [{\"confidence\": -1.24, \"length\": 0.48, \"start\": 0.9, \"word\": \"|?????????\"}, {\"confidence\": -2.36, \"length\": 0.32, \"start\": 1.38, \"word\": \"|?????????\"}, {\"confidence\": -4.18, \"length\": 1.16, \"start\": 1.7, \"word\": \"|???????????????.\"}, {\"confidence\": -1.96, \"length\": 0.24, \"start\": 2.86, \"word\": \"|??????\"}, {\"confidence\": -1.73, \"length\": 0.28, \"start\": 3.1, \"word\": \"|???\"}, {\"confidence\": -3.28, \"length\": 0.4, \"start\": 3.38, \"word\": \"|??????\"}, {\"confidence\": -4.22, \"length\": 0.56, \"start\": 3.78, \"word\": \"|?????????\"}, {\"confidence\": -2.32, \"length\": 0.2, \"start\": 4.34, \"word\": \"|???\"}, {\"confidence\": -9.72, \"length\": 0.8, \"start\": 4.54, \"word\": \"|???????????????\"}, {\"confidence\": -4.67, \"length\": 0.24, \"start\": 5.34, \"word\": \"|??????\"}, {\"confidence\": -4.07, \"length\": 0.16, \"start\": 5.58, \"word\": \"|???\"}, {\"confidence\": -0.35, \"length\": 0.4, \"start\": 5.89, \"word\": \"|?????????\"}, {\"confidence\": -0.66, \"length\": 0.2, \"start\": 6.29, \"word\": \"|???        ???\"}, {\"confidence\": -2.15, \"length\": 0.48, \"start\": 6.49, \"word\": \"|????????????\"}, {\"confidence\": -3.71, \"length\": 0.52, \"start\": 6.97, \"word\": \"|????????????\"}, {\"confidence\": -5.46, \"length\": 0.48, \"start\": 7.49, \"word\": \"|>?????????\"}, {\"confidence\": -6.37, \"length\": 0.36, \"start\": 7.97, \"word\": \"|?????????\"}, {\"confidence\": -6.92, \"length\": 1.16, \"start\": 8.33, \"word\": \"|?????????.\"}, {\"confidence\": -7.31, \"length\": 0.56, \"start\": 9.49, \"word\": \"|?????????\"}, {\"confidence\": -6.47, \"length\": 0.2, \"start\": 10.05, \"word\": \"|??????\"}, {\"confidence\": -2.41, \"length\": 0.28, \"start\": 10.4, \"word\": \"|??????\"}, {\"confidence\": -5.33, \"length\": 0.56, \"start\": 10.68, \"word\": \"|??????        ???,\"}, {\"confidence\": -5.76, \"length\": 0.52, \"start\": 11.24, \"word\": \"|?????????\"}, {\"confidence\": -5.78, \"length\": 0.4, \"start\": 11.76, \"word\": \"|??????\"}, {\"confidence\": -7.15, \"length\": 0.36, \"start\": 12.16, \"word\": \"|??????        ???\"}, {\"confidence\": -12.26, \"length\": 1.0, \"start\": 12.52, \"word\": \"|??????????????????.\"}, {\"confidence\": -10.04, \"length\": 0.48, \"start\": 13.52, \"word\": \"|?????????\"}, {\"confidence\": -12.0, \"length\": 0.36, \"start\": 14.0, \"word\": \"|?????????\"}, {\"confidence\": -18.64, \"length\": 0.4, \"start\": 14.36, \"word\": \"|???????????????.\"}]}]}, \"segment\": 0, \"segment-length\": 13.56, \"segment-start\": 0.9, \"speaker\": 0, \"status\": 0, \"total-length\": 14.84}"

        Log.e(TAG, "DPDPDP message:" + messageWithSpeakerNumStr)
        val gson = GsonBuilder().create()
        //val zerothMessage: ZerothMessage = gson.fromJson(message, ZerothMessage::class.java)
        val zerothMessage: ZerothMessage = gson.fromJson(messageWithSpeakerNumStr, ZerothMessage::class.java)

        //val zerothMessage: ZerothMessage = gson.fromJson(message, ZerothMessage::class.java)

        if (zerothMessage.getResult() == null || zerothMessage.getResult()?.getHypotheses() == null
        ) return;

        val transcript: String = zerothMessage.getResult()?.getHypotheses()!!.get(0)!!.getTranscript()!!
        val isFinal: Boolean = zerothMessage.getResult()?.getFinal()!!

        var startTime: Double = 0.0
        var endTime: Double = 0.0

        //mSpeakerNum = zerothMessage.getSpeakerNum()!!

        /*
        if( isFinal == true){ //?????? final?????? ????????? text??? ??? ?????????.
            Log.e("isFinal == true",transcript)
            mPrevSubtitleText = ""
            mCsSpeakerText!!.clear()
            viewModel.clearSpeakerText()
            mResetSubtitleHandler.removeMessages(MSG_SUBTILTILE_CUE_RESET)
            requireActivity().runOnUiThread(Runnable { mSzSubtitleView!!.text = "" })
            //this.SubtitleTextOutBySpeakerNum(true,"")
        }
        */
        if( isFinal == true)
        {
            try {
                mSpeakerNum = zerothMessage.getSpeakerNum()!!
                if( mSpeakerNum == null || mSpeakerNum < 0 ) mSpeakerNum =0;
                mSpeakerNum = (mSpeakerNum ) %10
            }
            catch (ex : Exception)
            {
                Log.e(TAG, "mSpeakerNum:" + mSpeakerNum )
            }


            val `object` = JSONObject()

            try {
                `object`.put("id", "signlanguage_final")
                `object`.put("time", 0.2)
                `object`.put("text", "??????+?????????")
                `object`.put("origin_text", "??????????????????")
            } catch (e: JSONException) {
                Log.e("NewsListFragment", "error parse json", e)
            } finally {
                UnityBridgeManager.getInstance()
                    .newUnitySendMessage("SetRecieveLyrics", `object`.toString())
                UnityBridgeManager.getInstance()
                    .newUnitySendMessage("SetRecieveLyrics", `object`.toString())
            }

            startTime = zerothMessage.getSegmentStart()!!
            endTime = zerothMessage.getTotalLength()!!

        }
        else
        {

        }
        viewModel.setTranscript(transcript,isFinal,mSpeakerNum!!,startTime,endTime)

        /*
        if (isFinal) {
            Log.e(TAG, "DPDPDP isFinal == true mPrevSubtitleText :" + mPrevSubtitleText  + "  transcript:" + transcript)

            mPrevSubtitleText = mPrevSubtitleText + "??????" + mSpeakerNum + ":" + transcript
            SubtitleTextOut(true, mPrevSubtitleText , mSpeakerNum!!)
            mPrevSubtitleText = mPrevSubtitleText + "\n"

            //20220722 cbw ??????????????? ?????? ??????
            //1. ???????????? ????????? ????????? ????????? ????????????.
            mCsSpeakerText!!.add(object : csSpeakerText(mSpeakerNum!! ,"??????" + mSpeakerNum + ":" + transcript + "\n" ,"??????"+ mSpeakerNum!! ,1){})

            //2. ????????? ????????? ????????? ????????? ????????????.
            mSpeakerNum = (mSpeakerNum!! +1 ) % 2
            //mSpeakerNum = zerothMessage.getSpeakerNum()
            if( mSpeakerNum == null) mSpeakerNum = 0
        } else {
            Log.e(TAG, "DPDPDP isFinal == false mPrevSubtitleText :" + mPrevSubtitleText  + "  transcript:" + transcript)

            mPartialText = transcript
            var text = "??????" + mSpeakerNum + ":" + mPartialText.replace("\n", " ")

            SubtitleTextOut(true, mPrevSubtitleText + text, mSpeakerNum!!)
        }
         */
    }


    /*
    //20220722 cbw ??????????????? ?????? ?????? - ???????????? ????????????
    private fun SubtitleTextOut(isApplyResetTimer: Boolean, msg: String?) {
        // Log.d(LOGTAG, type + " cueTextOut: " +msg);
        if (isApplyResetTimer) {
            mResetSubtitleHandler.removeMessages(MSG_SUBTILTILE_CUE_RESET)
            mResetSubtitleHandler.sendEmptyMessageDelayed(MSG_SUBTILTILE_CUE_RESET, 5000)
        }
        if (getActivity() != null && msg == null) {
            requireActivity().runOnUiThread(Runnable { mSzSubtitleView!!.text = "" })
        }
        val spannable: Spannable = SpannableString(msg)
        val backgroundColorSpan = BackgroundColorSpan(
            PlayerCaptionHelper.getColorWithAlpha(
                Color.BLACK,
                AppConfig.getInstance()?.convertPrefSubtitleTransparency(mSubtitleTransparency)!!
            )
        )
        spannable.setSpan(
            backgroundColorSpan,
            0,
            msg!!.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        if (getActivity() != null) {
            requireActivity().runOnUiThread(Runnable { mSzSubtitleView!!.text = spannable })
        }
    }
    */
    //20220722 cbw ??????????????? ?????? ?????? ???


    private val mResetSubtitleHandler: Handler = ResetSubtitleHandler(this)

    private class ResetSubtitleHandler(reference: BrowserFragment) : Handler(Looper.getMainLooper()) {
        private val mWeakReference: WeakReference<BrowserFragment> = WeakReference(reference)
        override fun handleMessage(msg: Message) {
            val fragement = mWeakReference.get()
            if (fragement != null) {
                when (msg.what) {
                    BrowserFragment.MSG_SUBTILTILE_CUE_RESET -> {
                        fragement.resetSubtitleView(true)
                    }
                }
            }
        }
    }

    fun resetSubtitleView(force: Boolean) {

        if (force) {
            mResetSubtitleHandler.removeMessages(MSG_SUBTILTILE_CUE_RESET)

            mPrevSubtitleText = ""
            mCsSpeakerText!!.clear()
            viewModel.clearSpeakerText()
            this.SubtitleTextOutBySpeakerNum(false,"")
            if( AppliactionUnity.getInstance()!!.getUnityView() != null) {
                AppliactionUnity.getInstance()!!.getUnityView()?.visibility = View.INVISIBLE

                AppliactionUnity.getInstance()!!.getUnityView()?.setTranslationY(  "-10000.0".toFloat() )
            }
            //tempFrameLayout?.visibility = View.INVISIBLE

            //AppliactionUnity.unityView!!.visibility = View.INVISIBLE
            //mUnityView!!.visibility = View.INVISIBLE

            /*
            //this.SubtitleTextOutBySpeakerNum(true,"")

            //mSzSubtitleView!!.visibility = View.INVISIBLE
            Handler().postDelayed({ //????????? ??? ????????? ?????? ??????

                mResetSubtitleHandler.removeMessages(MSG_SUBTILTILE_CUE_RESET)

                mPrevSubtitleText = ""
                mCsSpeakerText!!.clear()
                viewModel.clearSpeakerText()

                requireActivity().runOnUiThread(Runnable { mSzSubtitleView!!.text = "" })
                //AppliactionUnity.unityView!!.visibility = View.INVISIBLE

            }, 800)
            //2000822 cbw <-- ?????? ""??? ?????????, ?????? ????????? ????????? ????????? ?????????. ?????? ???????????? ?????????. ???????????? ????????? ????????? flaging??? ??????????????? flaging??? ????????? ?????? ???????????? ????????? ????????? ?????? ?????? ??????????????? ????????????.
*/
        }
    }


    /*
        fun getDisplayWidth(): Int {
            val displayWidth: Int
            val wm = requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = wm.defaultDisplay
            val metrics = DisplayMetrics()
            display.getMetrics(metrics)
            displayWidth = metrics.widthPixels
            return displayWidth
        }

        fun getDisplayHeight(): Int {
            val wm = requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val displaymetrics = DisplayMetrics()
            wm.defaultDisplay.getMetrics(displaymetrics)
            return displaymetrics.heightPixels
        }
    */








    private var pressed_x:Int = 0
    private var pressed_y:Int = 0

    private val captionTouchListener = View.OnTouchListener { _, event ->
        val layoutParams = mSzSubtitleView!!.layoutParams as RelativeLayout.LayoutParams
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                //  Log.d("TAG","@@@@ TV1 ACTION_UP");
                // Where the user started the drag
                pressed_x = event.rawX.toInt()
                pressed_y = event.rawY.toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                //    Log.d("TAG","@@@@ TV1 ACTION_UP");
                // Where the user's finger is during the drag
                val x = event.rawX.toInt()
                val y = event.rawY.toInt()

                // Calculate change in x and change in y
                //val dx = x - pressed_x
                val dy: Int = y - pressed_y

                // Update the margins
                // layoutParams.leftMargin += dx;
                val subtitleParent = mSzSubtitleView!!.parent as ViewGroup
                val textSize = UIUtils.dp2px(requireContext(), AppConfig.getInstance()?.convertPrefSubtitleFontSize(mSubtitleFontSize)!!.toFloat()).toFloat()
                val controlBarHeight = UIUtils.dp2px(requireContext(), 46.0f)
                val maxSubtitleHeight = (mSubtitleLine + 0.3f) * textSize

                /*
                if (layoutParams.topMargin + dy > controlBarHeight &&
                    layoutParams.topMargin + dy < subtitleParent.height - maxSubtitleHeight - controlBarHeight)

                 */

                if ( ( dy > 0 && layoutParams.topMargin + dy < subtitleParent.height - maxSubtitleHeight - controlBarHeight )
                    ||
                    ( dy < 0 && layoutParams.topMargin + dy > controlBarHeight ) )
                {
                    layoutParams.topMargin += dy

                    //   Log.d(TAG,"layoutParams.topMargin: " + layoutParams.topMargin);
                    mSzSubtitleView!!.layoutParams = layoutParams
                }

                // Save where the user's finger was for the next ACTION_MOVE
                pressed_x = x
                pressed_y = y
            }
            MotionEvent.ACTION_UP -> {
            }
        }
        false
    }

    // Setup subtitle
    private fun setupSubtitleView() {
        mSubtitleOnOff = AppConfig.getInstance()?.getPrefSubtitleOnOff()!!
        mSubtitlePoistion = AppConfig.getInstance()?.getPrefSubtitlePosition()!!
        mSubtitleLine = AppConfig.getInstance()?.getPrefSubtitleLine()!!
        mSubtitleFont = AppConfig.getInstance()?.getPrefSubtitleFont()!!
        mSubtitleFontSize = AppConfig.getInstance()?.getPrefSubtitleFontSize()!!
        mSubtitleForegroundColor = AppConfig.getInstance()?.getPrefSubtitleForegroundColor()!!
        mSubtitleTransparency = AppConfig.getInstance()?.getPrefSubtitleTransparency()!!
        val textSize = UIUtils.dp2px(requireContext(), AppConfig.getInstance()?.convertPrefSubtitleFontSize(mSubtitleFontSize)!!.toFloat()).toFloat()

        // ???????????????????????? ???????????? setup
        mSzSubtitleView!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        mSzSubtitleView!!.maxLines = mSubtitleLine
        mSzSubtitleView!!.setTextColor(mSubtitleForegroundColor) //
        mSzSubtitleView!!.setBackgroundColor(Color.TRANSPARENT) // ???????????? ????????????
        mSzSubtitleView!!.setShadowLayer(4f, 0f, 0f, Color.BLACK)
        mSzSubtitleView!!.setLineSpacing(0f, 1.0f) // ????????????
        //mSzSubtitleView.setMovementMethod(new ScrollingMovementMethod());
        mSzSubtitleView!!.movementMethod = createMovementMethod(requireContext())
        mSzSubtitleView!!.setTextIsSelectable(false)
        PlayerCaptionHelper.setSubtitleViewFont(requireContext(),
            mSzSubtitleView,
            AppConfig.getInstance()?.convertPrefSubtitleFontPath(requireContext(), mSubtitleFont))
        if (mSubtitleOnOff) {
            mSzSubtitleView!!.visibility = View.VISIBLE
        } else {
            mSzSubtitleView!!.visibility = View.INVISIBLE
        }

    }

    /**
     * subtitleview bottom ???????????? ???????????????????????????????????? ???????????? ?????????????????? ???????????? ???????????????????????? ????????????????????????
     *
     * @param context
     */
    @Suppress("UNUSED_PARAMETER")
    private fun createMovementMethod(context: Context): MovementMethod? {
/*
        val detector = GestureDetector(context, object : SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                return true
            }

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                return true
            }
        })

 */
        return object : ScrollingMovementMethod() {
            override fun onTouchEvent(widget: TextView, buffer: Spannable, event: MotionEvent): Boolean {
                return true
            }
        }
    }

    /**
     * reset subtitle position
     */
    fun resetSubtitlePositon() {
        Log.d(TAG, "resetSubtitlePositon: ")
        val layoutParams = mSzSubtitleView!!.layoutParams as RelativeLayout.LayoutParams
        val controlBarHeight = UIUtils.dp2px(requireContext(), 46.0f)
        if (mSubtitlePoistion == AppConfig.SUBTITLE_POSITION_TOP) {
            layoutParams.topMargin = controlBarHeight.toInt()
        } else {
            // ViewGroup subtitleParent = (ViewGroup)mSzSubtitleView.getParent();
            val textSize = UIUtils.dp2px(requireContext(), AppConfig.getInstance()?.convertPrefSubtitleFontSize(mSubtitleFontSize)!!.toFloat()).toFloat()
            var factor = 0.1f

            val orientation: Int = resources.configuration.orientation

            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                //factor = 1.3f
                factor = 2f
            }
            val maxSubtitleHeight = (mSubtitleLine + factor) * textSize
            layoutParams.topMargin = (DisplayUtil.getHeightPixels(requireContext()) - maxSubtitleHeight - controlBarHeight).toInt()

        }
        Log.d(TAG, "resetSubtitlePositon layoutParams.topMargin: " + layoutParams.topMargin)
        val textSize = UIUtils.dp2px(requireContext(), AppConfig.getInstance()?.convertPrefSubtitleFontSize(mSubtitleFontSize)!!.toFloat()).toFloat()
        //layoutParams.width =(DisplayUtil.getWidthPixels(requireContext()) - mUnityView!!.layoutParams.width).toInt()
        //layoutParams.width =(DisplayUtil.getWidthPixels(requireContext()) - AppliactionUnity.unityView!!.layoutParams.width).toInt()
        layoutParams.width =(DisplayUtil.getWidthPixels(requireContext()) - AppliactionUnity.getInstance()!!.getUnityView()!!.layoutParams.width - textSize ).toInt()

        mSzSubtitleView!!.layoutParams = layoutParams
    }

    /**
     * reset subtitle position
     */
    fun resetUnityViewPositon() {
        Log.d(TAG, "resetUnityViewPositon: ")

        //val layoutParams = AppliactionUnity.unityView!!.layoutParams as RelativeLayout.LayoutParams
        val layoutParams = AppliactionUnity.getInstance()!!.getUnityView()!!.layoutParams as RelativeLayout.LayoutParams

        //val layoutParams = mUnityView!!.layoutParams as RelativeLayout.LayoutParams

        val controlBarHeight = UIUtils.dp2px(requireContext(), 46.0f)
        if (mSubtitlePoistion == AppConfig.SUBTITLE_POSITION_TOP) {
            layoutParams.topMargin = controlBarHeight.toInt()
        } else {
            // ViewGroup subtitleParent = (ViewGroup)mSzSubtitleView.getParent();
            val textSize = UIUtils.dp2px(requireContext(), AppConfig.getInstance()?.convertPrefSubtitleFontSize(mSubtitleFontSize)!!.toFloat()).toFloat()
            var factor = 0.1f

            val orientation: Int = resources.configuration.orientation

            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                factor = 1.3f
            }
            val maxSubtitleHeight = (mSubtitleLine + factor) * textSize
            //layoutParams.topMargin = (DisplayUtil.getHeightPixels(requireContext()) - maxSubtitleHeight - controlBarHeight).toInt()
            //layoutParams.topMargin = (DisplayUtil.getHeightPixels(requireContext()) - AppliactionUnity.unityView!!.layoutParams.height - controlBarHeight).toInt()
            layoutParams.topMargin = (DisplayUtil.getHeightPixels(requireContext()) - AppliactionUnity.getInstance()!!.getUnityView()!!.layoutParams.height - controlBarHeight).toInt()

            //layoutParams.topMargin = (DisplayUtil.getHeightPixels(requireContext()) - mUnityView!!.layoutParams.height - controlBarHeight).toInt()

        }
        //layoutParams.leftMargin  = (DisplayUtil.getWidthPixels(requireContext()) - AppliactionUnity.unityView!!.layoutParams.width).toInt()
        layoutParams.leftMargin  = (DisplayUtil.getWidthPixels(requireContext()) - AppliactionUnity.getInstance()!!.getUnityView()!!.layoutParams.width).toInt()
        //layoutParams.leftMargin  = (DisplayUtil.getWidthPixels(requireContext()) - mUnityView!!.layoutParams.width).toInt()

        Log.d(TAG, "resetSubtitlePositon layoutParams.topMargin: " + layoutParams.topMargin)
        //AppliactionUnity.unityView!!.layoutParams = layoutParams
        AppliactionUnity.getInstance()!!.getUnityView()!!.layoutParams = layoutParams
        //mUnityView!!.layoutParams = layoutParams

    }
    /**
     * orientation ?????? ???????????? ???????????????????????? ????????????
     *
     * @param orientation
     */
    private fun toggleFullScreen(orientation: Int) {
        Log.d(TAG, "setPlayerConstraints orientation: $orientation")

        // ???????????? ?????????????????? ???????????? ???????????? ???????????????????????? ????????????
        val layoutParams = mSzSubtitleView!!.layoutParams as RelativeLayout.LayoutParams
        val controlBarHeight = UIUtils.dp2px(requireContext(), 46.0f)
        val textSize = UIUtils.dp2px(requireContext(), AppConfig.getInstance()?.convertPrefSubtitleFontSize(mSubtitleFontSize)!!.toFloat())
        var factor = 0.1f
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            factor = 1.3f
        }
        val maxSubtitleHeight = (mSubtitleLine + factor) * textSize
        val displayWidth = DisplayUtil.getWidthPixels(context)
        val displayHeight = DisplayUtil.getHeightPixels(context)

        layoutParams.topMargin = displayHeight * layoutParams.topMargin / displayWidth
        if (layoutParams.topMargin < controlBarHeight) layoutParams.topMargin = controlBarHeight.toInt()
        if (layoutParams.topMargin > displayHeight - maxSubtitleHeight - controlBarHeight) {
            layoutParams.topMargin = (displayHeight - maxSubtitleHeight - controlBarHeight).toInt()
        }
        mSzSubtitleView!!.layoutParams = layoutParams
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        // ???????????? ?????????????????? screen rotation
        toggleFullScreen(newConfig.orientation)
        Log.d(TAG, "onConfigurationChanged orientation: " + newConfig.orientation)
    }

    override fun onPause() {
        (activity as MainActivity?)?.callInitEndTime()
        super.onPause()
        if( AppliactionUnity.unityView!!.visibility == View.VISIBLE) {
            AppliactionUnity.getInstance()!!.getUnityView()?.visibility = View.INVISIBLE

        }


        AppliactionUnity.getInstance()!!.getUnityView()!!.pause()

        //AppliactionUnity.unityView!!.pause()
        //mUnityView!!.pause()
    }


    //20220722 cbw ??????????????? ??????
    // ???????????? ????????? ??????????????? ?????????. spannable??? setSpan??? ???????????? ???????????? ??????????????????
    open class csSpeakerText(var mSpeakerNum :Int, var mText : String?, var mSpeakerName : String, var mTextColor : Int)
    {
        /*
        var mSpeakerNum :Int = 0;
        var mText : String  = "";
        var mSpeakerName : String  = "??????1";  //????????? ??????
        var mTextColor : Int  = Color.BLACK;  //????????? ??????
        */
    }
}

