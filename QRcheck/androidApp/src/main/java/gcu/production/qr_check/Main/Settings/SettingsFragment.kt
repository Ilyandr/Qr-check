package gcu.production.qr_check.Main.Settings

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import gcu.production.qr_check.android.R
import gcu.production.qrcheck.StructureApp.GeneralStructure

internal class SettingsFragment
    : PreferenceFragmentCompat(), GeneralStructure
{
    private lateinit var settingsDAO: SettingsDAO

    override fun onCreatePreferences(
        savedInstanceState: Bundle?
        , rootKey: String?)
    {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        objectsInit()
        basicBehavior()
    }

    override fun objectsInit() {
        this.settingsDAO = SettingsService(this)
    }

    override fun basicBehavior()
    {
        findPreference<SwitchPreference>(getString(R.string.themeAppSettings))
            ?.setOnPreferenceChangeListener {
                    preference, newValue ->
                this.settingsDAO.launchSingleAction(
                    preference, newValue as Boolean
                )
            }

        findPreference<Preference>(getString(R.string.changeLoginSettings))
            ?.setOnPreferenceClickListener {
                this.settingsDAO.launchSingleAction(preference = it)
            }

        findPreference<Preference>(getString(R.string.changePasswordSettings))
            ?.setOnPreferenceClickListener {
                this.settingsDAO.launchSingleAction(preference = it)
            }

        findPreference<Preference>(getString(R.string.changeOrganizationSettings))
            ?.setOnPreferenceClickListener {
                this.settingsDAO.launchSingleAction(preference = it)
            }

        findPreference<Preference>(getString(R.string.removeAccountSettings))
            ?.setOnPreferenceClickListener {
                this.settingsDAO.launchSingleAction(preference = it)
            }

        findPreference<Preference>(getString(R.string.exitSettings))
            ?.setOnPreferenceClickListener {
                this.settingsDAO.launchSingleAction(preference = it)
            }
    }
}