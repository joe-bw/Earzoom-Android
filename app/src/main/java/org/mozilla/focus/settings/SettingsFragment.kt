/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.focus.settings

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import kr.co.sorizava.asrplayer.AsrConfigActivity
import kr.co.sorizava.asrplayer.SubtitleSettingActivity
import mozilla.components.browser.state.state.SessionState
import org.mozilla.focus.GleanMetrics.SettingsScreen
import org.mozilla.focus.R
import org.mozilla.focus.ext.requireComponents
import org.mozilla.focus.state.AppAction
import org.mozilla.focus.state.Screen
import org.mozilla.focus.telemetry.TelemetryWrapper
import org.mozilla.focus.utils.AppConstants
import org.mozilla.focus.utils.SupportUtils
import org.mozilla.focus.whatsnew.WhatsNew

class SettingsFragment : BaseSettingsFragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(bundle: Bundle?, s: String?) {
        setHasOptionsMenu(true)
        addPreferencesFromResource(R.xml.settings)

        if (!AppConstants.isDevBuild) {
            preferenceScreen.removePreference(findPreference(getString(R.string.pref_key_advanced_screen)))
        }

        // jhong
        // 음성인식 서버 제거
        // 고급 제거
        // Mozilla 제거
//        if (preferenceManager.findPreference<Preference>(getString(R.string.pref_key_asr_server_screen)) != null) {
//            preferenceScreen.removePreference(findPreference(getString(R.string.pref_key_asr_server_screen)))
//        }
//        if (preferenceManager.findPreference<Preference>(getString(R.string.pref_key_advanced_screen)) != null) {
//            preferenceScreen.removePreference(findPreference(getString(R.string.pref_key_advanced_screen)))
//        }
//        if (preferenceManager.findPreference<Preference>(getString(R.string.pref_key_mozilla_screen)) != null) {
//            preferenceScreen.removePreference(findPreference(getString(R.string.pref_key_mozilla_screen)))
//        }

//        preferenceScreen.removePreference(findPreference(getString(R.string.pref_key_advanced_screen)))
//        preferenceScreen.removePreference(findPreference(getString(R.string.pref_key_mozilla_screen)))

    }

    override fun onResume() {
        super.onResume()

        (requireActivity() as AppCompatActivity).supportActionBar?.customView

        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        updateTitle(R.string.menu_settings)
    }

    override fun onPause() {
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }

    override fun onPreferenceTreeClick(preference: androidx.preference.Preference): Boolean {
        val resources = resources

        val page = when (preference.key) {
            resources.getString(R.string.pref_key_asr_server_screen) -> {
                val intent = Intent().run {
                    setClass(requireContext(), AsrConfigActivity::class.java)
                }
                startActivity (intent)
                return super.onPreferenceTreeClick(preference)
            }
            //Screen.Settings.Page.AsrServer  // todo: sorizava  여기서 직접???


            resources.getString(R.string.pref_key_asr_caption_screen) -> {
                    val intent = Intent().run {
                        setClass(requireContext(), SubtitleSettingActivity::class.java)
                    }
                    startActivity (intent)
                    return super.onPreferenceTreeClick(preference)
            }
            //Screen.Settings.Page.Subtitle // todo: sorizava  여기서 직접???

            resources.getString(R.string.pref_key_general_screen) -> Screen.Settings.Page.General
            resources.getString(R.string.pref_key_privacy_security_screen) -> Screen.Settings.Page.Privacy
            resources.getString(R.string.pref_key_search_screen) -> Screen.Settings.Page.Search
            resources.getString(R.string.pref_key_advanced_screen) -> Screen.Settings.Page.Advanced
            resources.getString(R.string.pref_key_mozilla_screen) -> Screen.Settings.Page.Mozilla
            else -> throw IllegalStateException("Unknown preference: ${preference.key}")
        }

        requireComponents.appStore.dispatch(
            AppAction.OpenSettings(page)
        )

        return super.onPreferenceTreeClick(preference)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        TelemetryWrapper.settingsEvent(key, sharedPreferences.all[key].toString())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_settings_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_whats_new) {
            whatsNewClicked()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun whatsNewClicked() {
        val context = requireContext()

        SettingsScreen.whatsNewTapped.add()
        WhatsNew.userViewedWhatsNew(context)

        val sumoTopic = if (AppConstants.isKlarBuild)
            SupportUtils.SumoTopic.WHATS_NEW_KLAR
        else
            SupportUtils.SumoTopic.WHATS_NEW_FOCUS

        val url = SupportUtils.getSumoURLForTopic(context, sumoTopic)
        requireComponents.tabsUseCases.addTab(
            url,
            source = SessionState.Source.Internal.Menu,
            private = true
        )
    }

    companion object {
        const val TAG = "settings"

        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }
}
