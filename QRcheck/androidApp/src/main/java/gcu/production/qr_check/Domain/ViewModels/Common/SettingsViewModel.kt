@file:Suppress("PackageName")
package gcu.production.qr_check.Domain.ViewModels.Common

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import gcu.production.qr_check.Domain.Models.Common.SettingsModel
import gcu.production.qr_check.Domain.Models.default
import gcu.production.qr_check.Domain.Models.set
import gcu.production.qr_check.Repository.Data.MVVM.ViewModelRX
import gcu.production.qr_check.RestAPI.Features.RestInteraction.DataSourse.AccountDataSourceActions
import gcu.production.qr_check.Service.Repository.Base64.Base64Factory
import gcu.production.qr_check.Service.Repository.DataCorrectness
import gcu.production.qr_check.Service.Repository.DataStorageService
import gcu.production.qr_check.android.R
import gcu.production.qrcheck.AppEngine.EngineSDK
import gcu.production.qrcheck.RestAPI.Features.RestInteraction.restAPI
import kotlinx.coroutines.*

internal class SettingsViewModel(
    fragmentCall: FragmentActivity
): ViewModelRX<SettingsModel>()
{
    override val liveDataState =
        MutableLiveData<SettingsModel>()
            .default(SettingsModel.DefaultState)

    private val dataStorageService: DataStorageService by lazy {
        DataStorageService().init(fragmentCall)
    }

    init {
        registrationOfInteraction(fragmentCall)
    }

    override fun registrationOfInteraction(additionalData: FragmentActivity?) =
        additionalData?.let {
            this.liveDataState.observe(it)
            { stateNow ->
                when (stateNow)
                {
                    is SettingsModel.ThemeState ->
                        themeAppSettings(
                            stateNow.switchData
                        )

                    is SettingsModel.LoginState ->
                        changeSingleSetting(
                            stateNow.context
                            , AccountDataSourceActions.changePhone
                        ) { data ->
                            DataCorrectness.loginAction(
                                data as? String
                                , "8"
                            )
                        }

                    is SettingsModel.PasswordState ->
                        changeSingleSetting(
                            stateNow.context
                            , AccountDataSourceActions.changePassword
                        ) { data ->
                            DataCorrectness.passwordAction(
                                data as? String
                            )
                        }

                    is SettingsModel.OrganizationState ->
                        changeSingleSetting(
                            stateNow.context
                            , AccountDataSourceActions.changeOrganization
                        ) { data ->
                            DataCorrectness.commonAction(
                                data as? String
                            )
                        }

                    is SettingsModel.RemoveAccountState ->
                        changeSingleSetting(
                            stateNow.context ?: return@observe
                            , AccountDataSourceActions.deleteAccount
                        ) { data ->
                            DataCorrectness.passwordAction(
                                data as? String
                            )
                        }

                    is SettingsModel.ExitState ->
                        exitSettings(
                            stateNow.context ?: return@observe
                        ).show()

                    else -> return@observe
                }
            }
        }

    private fun themeAppSettings(switchData: Boolean) =
        this.dataStorageService.anyAction(
            DataStorageService.THEME_APP, switchData
        )

    private inline fun changeSingleSetting(
        context: Context
        , accountDataSourceAction: AccountDataSourceActions
        , crossinline dataCorrectnessAction: (checkedValue: Any?) -> Boolean
    ) {
        buildConfirmAlertDialog(context,
            (accountDataSourceAction != AccountDataSourceActions.changeOrganization
                    && accountDataSourceAction != AccountDataSourceActions.deleteAccount),
            accountDataSourceAction.getMessageID()
        ) { newValue, confirmValue ->
            if (!dataCorrectnessAction.invoke(newValue))
            {
                this.liveDataState.set(
                    SettingsModel.MessageChangeState(
                        R.string.toastErrorCommonCheckData
                    )
                )
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
                    liveDataState.set(
                        SettingsModel.MessageChangeState(
                            R.string.toastErrorCommonCheckData
                        )
                    )
                    return@buildConfirmAlertDialog
                }
            }

            val changeAsync: Deferred<Boolean?> =
                MainScope().async(Dispatchers.IO)
                {
                    liveDataState.set(
                        SettingsModel.LoadingState
                    )
                    EngineSDK
                        .restAPI
                        .restAccountRepository
                        .changeAccountSingleSetting(
                            accountDataSourceAction
                            , authAction()
                            , newValue ?: return@async false
                        )
                }

            MainScope().launch(Dispatchers.Main)
            {
                liveDataState.set(
                    SettingsModel.MessageChangeState(
                        if (changeAsync.await()!!) {
                            accountDataSourceAction actionAfterChange newValue
                            R.string.successChangeDataAccount
                        }
                        else
                            R.string.faultChangeDataAccount
                    )
                )
            }
        }.show()
    }

    private inline fun buildConfirmAlertDialog(
        context: Context
        , useConfirmData: Boolean
        , @StringRes messageID: Int
        , crossinline action: (
            inputNewData: String?,
            inputConfirm: String?) -> Unit
    ): AlertDialog
    {
        val layoutContainer = LinearLayout(context)
        val inputUserNewData = AppCompatEditText(context)
        val inputUserConfirmData = AppCompatEditText(context)

        layoutContainer.gravity = Gravity.CENTER
        layoutContainer.orientation = LinearLayout.VERTICAL

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

        return AlertDialog.Builder(context)
            .setMessage(messageID)
            .setView(layoutContainer)
            .setTitle(R.string.titleAlertConfirm)
            .setPositiveButton(R.string.textNextStageRegister)
            { dialog, _ ->
                action.invoke(
                    inputUserNewData.text.toString()
                    , inputUserConfirmData.text.toString()
                )
                dialog.cancel()
            }.setNegativeButton("Отмена") { dialog, _ -> dialog.cancel() }
            .create()
    }

    private infix fun exitSettings(context: Context) =
        AlertDialog.Builder(context)
            .setTitle(R.string.titleAlertConfirm)
            .setMessage(R.string.msgAlertExitAccount)
            .setPositiveButton("Выход")
            { dialog, _ ->
                if (this.dataStorageService.removeAllData())
                    liveDataState.set(
                        SettingsModel.ExitState(null)
                    )
                dialog.cancel()
            }
            .setNegativeButton("Отмена") { dialog, _ -> dialog.cancel() }
            .create()

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
                dataStorageService.actionWithAuth(
                    DataStorageService.LOGIN_ID
                    , newValue
                )
            AccountDataSourceActions.changePassword ->
                dataStorageService.actionWithAuth(
                    DataStorageService.PASSWORD_ID
                    , newValue
                )
            AccountDataSourceActions.deleteAccount ->
            {
                dataStorageService.removeAllData()
                liveDataState.set(
                    SettingsModel.RemoveAccountState(null)
                )
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