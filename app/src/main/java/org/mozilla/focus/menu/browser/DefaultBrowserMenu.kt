/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.focus.menu.browser

import android.content.Context
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import mozilla.components.browser.menu.WebExtensionBrowserMenuBuilder
import mozilla.components.browser.menu.item.BrowserMenuDivider
import mozilla.components.browser.menu.item.BrowserMenuImageSwitch
import mozilla.components.browser.menu.item.BrowserMenuImageText
import mozilla.components.browser.menu.item.BrowserMenuItemToolbar
import mozilla.components.browser.menu.item.TwoStateBrowserMenuImageText
import mozilla.components.browser.menu.item.WebExtensionPlaceholderMenuItem
import mozilla.components.browser.state.selector.selectedTab
import mozilla.components.browser.state.state.TabSessionState
import mozilla.components.browser.state.store.BrowserStore
import mozilla.components.feature.webcompat.reporter.WebCompatReporterFeature
import org.mozilla.focus.R
import org.mozilla.focus.bookmark.BookmarkProvider
import org.mozilla.focus.extension.showToast
import org.mozilla.focus.menu.ToolbarMenu
import org.mozilla.focus.state.AppStore
import org.mozilla.focus.theme.resolveAttribute
import org.mozilla.focus.topsites.DefaultTopSitesStorage.Companion.TOP_SITES_MAX_LIMIT
import org.mozilla.focus.utils.ToastMessage
import org.mozilla.focus.utils.UrlUtils

/**
 * The overflow menu shown in the BrowserFragment containing page actions like "Refresh", "Share" etc.
 */
class DefaultBrowserMenu(
    private val context: Context,
    private val appStore: AppStore,
    private val store: BrowserStore,
    private val isPinningSupported: Boolean,
    private val onItemTapped: (ToolbarMenu.Item) -> Unit = {}
) : ToolbarMenu {

    private val selectedSession: TabSessionState?
        get() = store.state.selectedTab

    override val menuBuilder by lazy {
        WebExtensionBrowserMenuBuilder(
            items = mvpMenuItems, store = store, showAddonsInMenu = false
        )
    }

    override val menuToolbar by lazy {
        val back = BrowserMenuItemToolbar.TwoStateButton(
            primaryImageResource = R.drawable.mozac_ic_back,
            primaryContentDescription = context.getString(R.string.content_description_back),
            primaryImageTintResource = context.theme.resolveAttribute(R.attr.primaryText),
            isInPrimaryState = {
                selectedSession?.content?.canGoBack ?: true
            },
            secondaryImageTintResource = context.theme.resolveAttribute(R.attr.disabled),
            disableInSecondaryState = true,
            longClickListener = { onItemTapped.invoke(ToolbarMenu.Item.Back) }
        ) {
            onItemTapped.invoke(ToolbarMenu.Item.Back)
        }

        val forward = BrowserMenuItemToolbar.TwoStateButton(
            primaryImageResource = R.drawable.mozac_ic_forward,
            primaryContentDescription = context.getString(R.string.content_description_forward),
            primaryImageTintResource = context.theme.resolveAttribute(R.attr.primaryText),
            isInPrimaryState = {
                selectedSession?.content?.canGoForward ?: true
            },
            secondaryImageTintResource = context.theme.resolveAttribute(R.attr.disabled),
            disableInSecondaryState = true,
            longClickListener = { onItemTapped.invoke(ToolbarMenu.Item.Forward) }
        ) {
            onItemTapped.invoke(ToolbarMenu.Item.Forward)
        }

        val bookmark = BrowserMenuItemToolbar.TwoStateButton(
            primaryImageResource = R.drawable.ic_bookmark_outline,
            primaryContentDescription = context.getString(R.string.content_description_bookmark_add),
            primaryImageTintResource = context.theme.resolveAttribute(R.attr.primaryText),
            isInPrimaryState = {
                !BookmarkProvider.isBookmarkedUrl(context.contentResolver, selectedSession?.content?.url as String)
            },
            secondaryImageResource = R.drawable.ic_bookmark_filled,
            secondaryContentDescription = context.getString(R.string.content_description_bookmark_remove),
            secondaryImageTintResource = context.theme.resolveAttribute(R.attr.primaryText),
            disableInSecondaryState = false,
            longClickListener = {  }
        ) {
            val currentUrl = selectedSession?.content?.url as String
            val currentTitle = selectedSession?.content?.title as String

            val isCurrentUrlBookmarked = BookmarkProvider.isBookmarkedUrl(context.contentResolver, currentUrl)

            if (isCurrentUrlBookmarked) {
                BookmarkProvider.deleteBookmarkByUrl(context.contentResolver, currentUrl)
                context.showToast(ToastMessage(R.string.bookmark_removed, duration = Toast.LENGTH_LONG))
            } else {
                if (!currentUrl.isNullOrEmpty()) {
                    val title = currentTitle.takeUnless { it.isNullOrEmpty() }
                        ?: UrlUtils.stripCommonSubdomains(UrlUtils.stripHttp(currentUrl))

                    BookmarkProvider.addOrUpdateItem(context.contentResolver, title, currentUrl, 0);
                    context.showToast(ToastMessage(R.string.bookmark_saved, duration = Toast.LENGTH_LONG))
                }
            }
        }

        val refresh = BrowserMenuItemToolbar.TwoStateButton(
            primaryImageResource = R.drawable.mozac_ic_refresh,
            primaryContentDescription = context.getString(R.string.content_description_reload),
            primaryImageTintResource = context.theme.resolveAttribute(R.attr.primaryText),
            isInPrimaryState = {
                selectedSession?.content?.loading == false
            },
            secondaryImageResource = R.drawable.mozac_ic_stop,
            secondaryContentDescription = context.getString(R.string.content_description_stop),
            secondaryImageTintResource = context.theme.resolveAttribute(R.attr.primaryText),
            disableInSecondaryState = false,
            longClickListener = { onItemTapped.invoke(ToolbarMenu.Item.Reload) }
        ) {
            if (selectedSession?.content?.loading == true) {
                onItemTapped.invoke(ToolbarMenu.Item.Stop)
            } else {
                onItemTapped.invoke(ToolbarMenu.Item.Reload)
            }
        }


        val share = BrowserMenuItemToolbar.Button(
            imageResource = R.drawable.mozac_ic_share,
            contentDescription = context.getString(R.string.menu_share),
            listener = {
                onItemTapped.invoke(ToolbarMenu.Item.Share)
            }
        )
        BrowserMenuItemToolbar(listOf(back, forward, bookmark, share, refresh))
    }

    private val mvpMenuItems by lazy {

        val shortcuts = TwoStateBrowserMenuImageText(
            primaryLabel = context.getString(R.string.menu_add_to_shortcuts),
            primaryStateIconResource = R.drawable.mozac_ic_pin,
            secondaryLabel = context.getString(R.string.menu_remove_from_shortcuts),
            secondaryStateIconResource = R.drawable.mozac_ic_pin_remove,
            isInPrimaryState = {
                appStore.state.topSites.find { it.url == selectedSession?.content?.url } == null &&
                    selectedSession?.content?.url != null && appStore.state.topSites.size < TOP_SITES_MAX_LIMIT
            },
            isInSecondaryState = {
                appStore.state.topSites.find { it.url == selectedSession?.content?.url } != null
            },
            primaryStateAction = { onItemTapped.invoke(ToolbarMenu.Item.AddToShortcuts) },
            secondaryStateAction = { onItemTapped.invoke(ToolbarMenu.Item.RemoveFromShortcuts) },
        )

        val shortcutsDivider = BrowserMenuDivider().apply {
            visible = shortcuts.visible
        }

        val findInPage = BrowserMenuImageText(
            label = context.getString(R.string.find_in_page),
            imageResource = R.drawable.mozac_ic_search
        ) {
            onItemTapped.invoke(ToolbarMenu.Item.FindInPage)
        }

        val desktopMode = BrowserMenuImageSwitch(
            imageResource = R.drawable.mozac_ic_device_desktop,
            label = context.getString(R.string.preference_performance_request_desktop_site2),
            initialState = {
                selectedSession?.content?.desktopMode ?: false
            }
        ) { checked ->
            onItemTapped.invoke(ToolbarMenu.Item.RequestDesktop(checked))
        }

        val reportSiteIssuePlaceholder = WebExtensionPlaceholderMenuItem(
            id = WebCompatReporterFeature.WEBCOMPAT_REPORTER_EXTENSION_ID,
            iconTintColorResource = context.theme.resolveAttribute(R.attr.primaryText)
        )

        // add: sorizava bookmark
        val openBookmarks = BrowserMenuImageText(
            label = context.getString(R.string.menu_bookmarks),
            imageResource = R.drawable.ic_bookmark_filled
        ) {
            onItemTapped.invoke(ToolbarMenu.Item.Bookmarks)
        }


        @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
        fun canAddToHomescreen(): Boolean =
            selectedSession != null && isPinningSupported

        val addToHomescreen = BrowserMenuImageText(
            label = context.getString(R.string.menu_add_to_home_screen),
            imageResource = R.drawable.mozac_ic_add_to_home_screen
        ) {
            onItemTapped.invoke(ToolbarMenu.Item.AddToHomeScreen)
        }

        val openInApp = BrowserMenuImageText(
            label = context.getString(R.string.menu_open_with_a_browser2),
            imageResource = R.drawable.mozac_ic_open_in,
            textColorResource = context.theme.resolveAttribute(R.attr.primaryText)
        ) {
            onItemTapped.invoke(ToolbarMenu.Item.OpenInApp)
        }

        val settings = BrowserMenuImageText(
            label = context.getString(R.string.menu_settings),
            imageResource = R.drawable.mozac_ic_settings,
            textColorResource = context.theme.resolveAttribute(R.attr.primaryText)
        ) {
            onItemTapped.invoke(ToolbarMenu.Item.Settings)
        }

        listOfNotNull(
            menuToolbar,
            BrowserMenuDivider(),
            shortcuts,
            shortcutsDivider,
            findInPage,
            desktopMode,
            reportSiteIssuePlaceholder,
            BrowserMenuDivider(),
            openBookmarks,
            addToHomescreen.apply { visible = ::canAddToHomescreen },
            openInApp,
            BrowserMenuDivider(),
            settings
        )
    }
}
