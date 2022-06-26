package gcu.production.qr_check.Main.Settings

import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.Navigation
import androidx.preference.Preference
import gcu.production.qr_check.Service.DataStorageService
import gcu.production.qr_check.Service.NetworkConnection
import gcu.production.qr_check.android.R
import gcu.production.qrcheck.android.GeneralAppUI.CustomLoadingDialog

internal class SettingsService(
    private val parentFragment: SettingsFragment): SettingsDAO
{
    private val loadingDialog: CustomLoadingDialog by lazy {
        CustomLoadingDialog(
            this.parentFragment.requireActivity()
        )
    }

    private val dataStorageService: DataStorageService by lazy {
        DataStorageService(
            this.parentFragment.requireContext()
        )
    }

    override fun launchSingleAction(
        preference: Preference
        ,  switchData: Boolean?
        , returnValue: Boolean): Boolean
    {
        when (preference.key)
        {
            this.parentFragment.getString(R.string.themeAppSettings) ->
                themeAppSettings(
                    switchData ?: return returnValue
                )
            this.parentFragment.getString(R.string.changeLoginSettings) ->
                launchWithCheckNetworkConnection(
                    ::changeLoginSettings
                )
            this.parentFragment.getString(R.string.changePasswordSettings) ->
                launchWithCheckNetworkConnection(
                    ::changePasswordSettings
                )
            this.parentFragment.getString(R.string.changeOrganizationSettings) ->
                launchWithCheckNetworkConnection(
                    ::changeOrganizationSettings
                )
            this.parentFragment.getString(R.string.removeAccountSettings) ->
                launchWithCheckNetworkConnection(
                    ::removeAccountSettings
                )
            this.parentFragment.getString(R.string.exitSettings) ->
                exitSettings()
        }
        return returnValue
    }

    private fun themeAppSettings(switchData: Boolean) =
        this.dataStorageService.anyAction(
            DataStorageService.THEME_APP, switchData
        )?.let {
            Toast.makeText(
                this.parentFragment.requireContext()
                , this.parentFragment
                    .getString(R.string.toastSuccessChangeTheme)
                , Toast.LENGTH_SHORT)
                .show()
        }

    private fun changeLoginSettings()
    {

    }

    private fun changePasswordSettings()
    {

    }

    private fun changeOrganizationSettings()
    {

    }

    private fun removeAccountSettings()
    {

    }

    private fun exitSettings() =
        AlertDialog.Builder(
            this.parentFragment.requireActivity()
        ).setTitle(parentFragment.getString(R.string.titleAlertConfirm))
            .setMessage(parentFragment.getString(R.string.msgAlertExitAccount))
            .setPositiveButton("Выход") {
                    dialog, _ ->
                if (this.dataStorageService.removeAllData())
                    Navigation.findNavController(
                        this.parentFragment.view!!)
                        .navigate(R.id.actionExitAccount)
                dialog.cancel()
            }
            .setNegativeButton("Отмена") {
                    dialog, _ -> dialog.cancel()
            }.create().show()

    private inline fun launchWithCheckNetworkConnection(
        crossinline actionSuccess: () -> Unit) =
        NetworkConnection
            .checkingAccessWithActions(
                actionSuccess = actionSuccess
                , actionFault =
                {
                    Toast.makeText(
                        this.parentFragment.requireContext()
                        , R.string.toastErrorNetwork
                        , Toast.LENGTH_SHORT)
                        .show()
                }
                , actionsLoadingAfterAndBefore =  Pair(
                    kotlinx.coroutines.Runnable {
                        this.loadingDialog.startLoadingDialog() }
                    , kotlinx.coroutines.Runnable {
                        this.loadingDialog.stopLoadingDialog() }
                )
            )
}