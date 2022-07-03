@file:Suppress("PackageName")
package gcu.production.qr_check.Main.Settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import gcu.production.qr_check.GeneralAppUI.ActionBarSettings.setBarOptions
import gcu.production.qr_check.android.R
import gcu.production.qrcheck.StructureApp.GeneralStructure

internal class SettingsFragment
    : PreferenceFragmentCompat(), GeneralStructure
{
    private val settingsServiceImpl: SettingsServiceImpl by lazy {
        SettingsService(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View?
    {
        requireActivity().setBarOptions(
            R.string.titleSettingsFragment
            , true
        )

        return super.onCreateView(
            inflater
            , container
            , savedInstanceState
        )
    }

    override fun onCreatePreferences(
        savedInstanceState: Bundle?
        , rootKey: String?)
    {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        basicBehavior()
    }

    override fun objectsInit() { }

    override fun basicBehavior()
    {
        findPreference<SwitchPreference>(getString(R.string.themeAppSettings))
            ?.setOnPreferenceChangeListener {
                    preference, newValue ->
                this.settingsServiceImpl.launchSingleAction(
                    preference, newValue as Boolean
                )
            }

        findPreference<Preference>(getString(R.string.changeLoginSettings))
            ?.setOnPreferenceClickListener {
                this.settingsServiceImpl.launchSingleAction(preference = it)
            }

        findPreference<Preference>(getString(R.string.changePasswordSettings))
            ?.setOnPreferenceClickListener {
                this.settingsServiceImpl.launchSingleAction(preference = it)
            }

        findPreference<Preference>(getString(R.string.changeOrganizationSettings))
            ?.setOnPreferenceClickListener {
                this.settingsServiceImpl.launchSingleAction(preference = it)
            }

        findPreference<Preference>(getString(R.string.removeAccountSettings))
            ?.setOnPreferenceClickListener {
                this.settingsServiceImpl.launchSingleAction(preference = it)
            }

        findPreference<Preference>(getString(R.string.exitSettings))
            ?.setOnPreferenceClickListener {
                this.settingsServiceImpl.launchSingleAction(preference = it)
            }
    }
}