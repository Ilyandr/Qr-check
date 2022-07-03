@file:Suppress("PackageName")
@file:OptIn(DelicateCoroutinesApi::class)

package gcu.production.qr_check.Main.Settings

import android.content.Intent
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import androidx.navigation.Navigation
import androidx.preference.Preference
import gcu.production.qr_check.BasicActivity
import gcu.production.qr_check.RestAPI.Features.RestInteraction.DataSourse.AccountDataSourceActions
import gcu.production.qr_check.Service.Base64.Base64Factory
import gcu.production.qr_check.Service.DataStorageService
import gcu.production.qr_check.Service.NetworkConnection
import gcu.production.qr_check.android.R
import gcu.production.qrcheck.AppEngine.EngineSDK
import gcu.production.qrcheck.RestAPI.Features.RestInteraction.restAPI
import gcu.production.qrcheck.Service.DataCorrectness
import gcu.production.qrcheck.android.GeneralAppUI.CustomLoadingDialog
import kotlinx.coroutines.*

internal class SettingsService(
    private val parentFragment: SettingsFragment): SettingsServiceImpl
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
               changeSingleSetting(AccountDataSourceActions.changePhone) {
                   DataCorrectness.loginAction(it as String, "8")
               }
            this.parentFragment.getString(R.string.changePasswordSettings) ->
                changeSingleSetting(AccountDataSourceActions.changePassword) {
                    DataCorrectness.passwordAction(it as String)
                }
            this.parentFragment.getString(R.string.changeOrganizationSettings) ->
                changeSingleSetting(AccountDataSourceActions.changeOrganization) {
                    DataCorrectness.commonAction(it as String)
                }
            this.parentFragment.getString(R.string.removeAccountSettings) ->
                changeSingleSetting(AccountDataSourceActions.deleteAccount) {
                    DataCorrectness.passwordAction(it as String)
                }
            this.parentFragment.getString(R.string.exitSettings) ->
                exitSettings().show()
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

    private inline fun changeSingleSetting(
        accountDataSourceAction: AccountDataSourceActions
        , crossinline dataCorrectnessAction: (checkedValue: Any?) -> Boolean
    ) {
        buildConfirmAlertDialog(
            (accountDataSourceAction != AccountDataSourceActions.changeOrganization
                    && accountDataSourceAction != AccountDataSourceActions.deleteAccount)
            ,accountDataSourceAction.getMessageID()
        ) { newValue, confirmValue ->
            if (!dataCorrectnessAction.invoke(newValue))
            {
                Toast.makeText(
                    parentFragment.requireContext()
                    , R.string.toastErrorCommonCheckData
                    , Toast.LENGTH_SHORT)
                    .show()
                return@buildConfirmAlertDialog
            }

            if ((accountDataSourceAction != AccountDataSourceActions.changeOrganization)
                && (accountDataSourceAction != AccountDataSourceActions.deleteAccount))
            {
                if (!dataCorrectnessAction.invoke(confirmValue)
                    || !this.dataStorageService.actionWithAuth(
                        if (accountDataSourceAction == AccountDataSourceActions.deleteAccount
                            || accountDataSourceAction == AccountDataSourceActions.changePassword)
                            DataStorageService.PASSWORD_ID
                        else
                            DataStorageService.LOGIN_ID
                        , null)?.equals(confirmValue)!!
                ) {
                    showFaultToast()
                    return@buildConfirmAlertDialog
                }
            }

            launchWithCheckNetworkConnection {
                val changeAsync: Deferred<Boolean?> =
                    GlobalScope.async(Dispatchers.IO)
                    {
                        loadingDialog.startLoadingDialog()

                        EngineSDK
                            .restAPI
                            .restAccountRepository
                            .changeAccountSingleSetting(
                                accountDataSourceAction
                                , authAction()
                                , newValue ?: return@async false
                            )
                    }

                GlobalScope.launch(Dispatchers.Main)
                {
                    Toast.makeText(
                        parentFragment.requireContext()
                        , if (changeAsync.await()!!)
                        {
                            accountDataSourceAction actionAfterChange newValue
                            R.string.successChangeDataAccount
                        }
                        else
                            R.string.faultChangeDataAccount
                        , Toast.LENGTH_LONG
                    ).show()
                    loadingDialog.stopLoadingDialog()
                }
            }
        }.show()
    }

    private fun showFaultToast() =
        Toast.makeText(
            parentFragment.requireContext()
            , R.string.toastErrorCommonCheckData
            , Toast.LENGTH_SHORT)
            .show()

    private inline fun buildConfirmAlertDialog(
        useConfirmData: Boolean
        , @StringRes messageID: Int
        , crossinline action: (
            inputNewData: String?
            , inputConfirm: String?) -> Unit
    ): AlertDialog
    {
        val layoutContainer =
            LinearLayout(
                this.parentFragment.requireContext()
            )

        layoutContainer.gravity = Gravity.CENTER
        layoutContainer.orientation = LinearLayout.VERTICAL

        val inputUserNewData =
            AppCompatEditText(
                this.parentFragment.requireContext()
            )

        val inputUserConfirmData =
            AppCompatEditText(
                this.parentFragment.requireContext()
            )

        if (useConfirmData)
        {
            inputUserConfirmData.hint = "Предыдущие данные"
            inputUserConfirmData.gravity = Gravity.CENTER
            layoutContainer.addView(inputUserConfirmData)
        }

        inputUserNewData.hint =
            if (messageID != R.string.alertSettingsDeleteAccount)
                "Новые данные"
            else
                "Текущие данные"
        inputUserNewData.gravity = Gravity.CENTER
        layoutContainer.addView(inputUserNewData)

        return AlertDialog.Builder(
            this.parentFragment.requireContext()
        ).setMessage(messageID)
            .setView(layoutContainer)
            .setTitle(
                parentFragment.getString(R.string.titleAlertConfirm)
            ).setPositiveButton(
                parentFragment.getString(R.string.textNextStageRegister)
            ) { dialog, _ ->
                    action.invoke(
                        inputUserNewData.text.toString()
                        , inputUserConfirmData.text.toString()
                    )
                dialog.cancel()
            }.setNegativeButton("Отмена") { dialog, _ ->
                dialog.cancel()
            }.create()
    }


    private fun exitSettings() =
        AlertDialog.Builder(
            this.parentFragment.requireActivity()
        ).setTitle(
            parentFragment.getString(R.string.titleAlertConfirm)
        )
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
            }.create()

    private inline fun launchWithCheckNetworkConnection(
        crossinline actionSuccess: () -> Unit
    ) =
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
                    Runnable {
                        this.loadingDialog.startLoadingDialog() }
                    , Runnable {
                        this.loadingDialog.stopLoadingDialog() }
                )
            )

    private fun AccountDataSourceActions.getMessageID() =
        when(this)
        {
            AccountDataSourceActions.changePhone ->
                R.string.alertSettingsChangeLogin

            AccountDataSourceActions.changePassword ->
                R.string.alertSettingsChangePassword

            AccountDataSourceActions.changeOrganization ->
                R.string.alertSettingsChangeOrganization

            AccountDataSourceActions.deleteAccount ->
                R.string.alertSettingsDeleteAccount
        }

    private infix fun AccountDataSourceActions
            .actionAfterChange(newValue: String?)
    {
        when(this)
        {
            AccountDataSourceActions.changePhone ->
                this@SettingsService
                    .dataStorageService
                    .actionWithAuth(
                        DataStorageService.LOGIN_ID, newValue
                    )
            AccountDataSourceActions.changePassword ->
                this@SettingsService
                    .dataStorageService
                    .actionWithAuth(
                        DataStorageService.PASSWORD_ID, newValue
                    )
            AccountDataSourceActions.deleteAccount ->
            {
                this@SettingsService
                    .dataStorageService
                    .removeAllData()

                this@SettingsService
                    .parentFragment
                    .requireActivity()
                    .let {
                        it.finish()
                        it.startActivity(
                            Intent(
                                it, BasicActivity::class.java
                            )
                        )
                    }
            }

            else -> return
        }
    }

    private fun authAction() =
        Base64Factory
            .createEncoder()
            .encodeToString(("${dataStorageService.actionWithAuth(
                DataStorageService.LOGIN_ID, null)}" +
                    ":${dataStorageService.actionWithAuth(
                        DataStorageService.PASSWORD_ID, null)}"
                    ).toByteArray()
            )
}