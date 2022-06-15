/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.focus.browser.integration

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import mozilla.components.browser.state.selector.findCustomTabOrSelectedTab
import mozilla.components.browser.state.store.BrowserStore
import mozilla.components.browser.toolbar.BrowserToolbar
import mozilla.components.feature.session.SessionUseCases
import mozilla.components.lib.state.ext.flowScoped
import mozilla.components.support.base.feature.LifecycleAwareFeature
import mozilla.components.support.ktx.kotlinx.coroutines.flow.ifAnyChanged
import mozilla.components.support.utils.ColorUtils
import org.mozilla.focus.R
import org.mozilla.focus.ext.ifCustomTab
import org.mozilla.focus.bookmark.BookmarkProvider
import org.mozilla.focus.extension.showToast
import org.mozilla.focus.theme.resolveAttribute
import org.mozilla.focus.utils.ToastMessage
import org.mozilla.focus.utils.UrlUtils

class NavigationButtonsIntegration(
    val context: Context,
    val store: BrowserStore,
    val toolbar: BrowserToolbar,
    private val sessionUseCases: SessionUseCases,
    private val customTabId: String?
) : LifecycleAwareFeature {
    private var scope: CoroutineScope? = null

    private var enabledColorRes = context.theme.resolveAttribute(R.attr.primaryText)
    private var disabledColorRes = context.theme.resolveAttribute(R.attr.disabled)

    init {
        store.state.findCustomTabOrSelectedTab(customTabId)?.ifCustomTab()?.let { sessionState ->
            sessionState.config.toolbarColor?.let { color ->
                if (!ColorUtils.isDark(color)) {
                    enabledColorRes = R.color.enabled_toolbar_button_color
                    disabledColorRes = R.color.disabledText
                }
            }
        }

        val backButton = BrowserToolbar.TwoStateButton(
            primaryImage = ContextCompat.getDrawable(context, R.drawable.mozac_ic_back)!!,
            primaryContentDescription = context.getString(R.string.content_description_back),
            primaryImageTintResource = enabledColorRes,
            isInPrimaryState = {
                store.state.findCustomTabOrSelectedTab(customTabId)?.content?.canGoBack
                    ?: false
            },
            secondaryImageTintResource = disabledColorRes,
            disableInSecondaryState = true,
            longClickListener = null,
            listener = {
                sessionUseCases.goBack(store.state.findCustomTabOrSelectedTab(customTabId)?.id)
            }
        )
        toolbar.addNavigationAction(backButton)

        val forwardButton = BrowserToolbar.TwoStateButton(
            primaryImage = ContextCompat.getDrawable(context, R.drawable.mozac_ic_forward)!!,
            primaryContentDescription = context.getString(R.string.content_description_forward),
            primaryImageTintResource = enabledColorRes,
            isInPrimaryState = {
                store.state.findCustomTabOrSelectedTab(customTabId)?.content?.canGoForward
                    ?: false
            },
            secondaryImageTintResource = disabledColorRes,
            disableInSecondaryState = true,
            longClickListener = null,
            listener = {
                sessionUseCases.goForward(store.state.findCustomTabOrSelectedTab(customTabId)?.id)
            }
        )
        toolbar.addNavigationAction(forwardButton)
/*
        // sorizava: libray dont fix
        val toogleBookmarkButton = BrowserToolbar.TwoStateButton(
            primaryImage = ContextCompat.getDrawable(context, R.drawable.ic_bookmark_outline)!!,
            secondaryImage = ContextCompat.getDrawable(context, R.drawable.ic_bookmark_filled)!!,
            primaryContentDescription = context.getString(R.string.content_description_bookmark_add),
            secondaryContentDescription = context.getString(R.string.content_description_bookmark_remove),
            primaryImageTintResource = enabledColorRes,
            isInPrimaryState = {
                !BookmarkProvider.isBookmarkedUrl(context.contentResolver, store.state.findCustomTabOrSelectedTab(customTabId)?.content?.url as String)
            },
            secondaryImageTintResource = enabledColorRes,
            disableInSecondaryState = false,
            longClickListener = null,
            listener = {
                val tab = store.state.findCustomTabOrSelectedTab(customTabId)
                    ?: return@TwoStateButton

                val currentUrl = tab.content.url
                val isCurrentUrlBookmarked = BookmarkProvider.isBookmarkedUrl(context.contentResolver, store.state.findCustomTabOrSelectedTab(customTabId)?.content?.url as String)

                if (isCurrentUrlBookmarked) {

                    BookmarkProvider.deleteBookmarkByUrl(context.contentResolver, currentUrl)
                    context.showToast(ToastMessage(R.string.bookmark_removed, duration = Toast.LENGTH_LONG))
                } else {

                    val currentTitle = tab.content.title

                    if (!currentUrl.isNullOrEmpty()) {
                        val title = currentTitle.takeUnless { it.isNullOrEmpty() }
                            ?: UrlUtils.stripCommonSubdomains(UrlUtils.stripHttp(currentUrl))

                        BookmarkProvider.addOrUpdateItem(context.contentResolver, title, currentUrl, 0);
                        context.showToast(ToastMessage(R.string.bookmark_saved, duration = Toast.LENGTH_LONG))
                    }
                }
            }
        )
        toolbar.addNavigationAction(toogleBookmarkButton)
*/
        val reloadOrStopButton = BrowserToolbar.TwoStateButton(
            primaryImage = ContextCompat.getDrawable(context, R.drawable.mozac_ic_stop)!!,
            secondaryImage = ContextCompat.getDrawable(context, R.drawable.mozac_ic_refresh)!!,
            primaryContentDescription = context.getString(R.string.content_description_stop),
            secondaryContentDescription = context.getString(R.string.content_description_reload),
            primaryImageTintResource = enabledColorRes,
            isInPrimaryState = {
                store.state.findCustomTabOrSelectedTab(customTabId)?.content?.loading ?: false
            },
            secondaryImageTintResource = enabledColorRes,
            disableInSecondaryState = false,
            longClickListener = null,
            listener = {
                val tab = store.state.findCustomTabOrSelectedTab(customTabId)
                    ?: return@TwoStateButton
                if (tab.content.loading) {
                    sessionUseCases.stopLoading(tab.id)
                } else {
                    sessionUseCases.reload(tab.id)
                }
            }
        )
        toolbar.addNavigationAction(reloadOrStopButton)
    }

    override fun start() {
        scope = store.flowScoped { flow ->
            flow.map { state -> state.findCustomTabOrSelectedTab(customTabId) }
                .ifAnyChanged { tab ->
                    arrayOf(
                        tab?.content?.canGoBack,
                        tab?.content?.canGoForward,
                        tab?.content?.loading
                    )
                }
                .collect { toolbar.invalidateActions() }
        }
    }

    override fun stop() {
        scope?.cancel()
    }
}
