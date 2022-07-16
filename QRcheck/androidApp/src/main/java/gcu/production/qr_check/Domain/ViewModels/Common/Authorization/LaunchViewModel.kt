@file:Suppress("PackageName")
package gcu.production.qr_check.Domain.ViewModels.Common.Authorization

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import gcu.production.qr_check.Domain.Models.Common.Authorization.LaunchModel
import gcu.production.qr_check.Domain.Models.default
import gcu.production.qr_check.Domain.Models.set
import gcu.production.qr_check.Repository.Data.MVVM.ViewModelRX
import gcu.production.qr_check.Service.Repository.DataStorageService
import gcu.production.qr_check.Service.Repository.NetworkConnection
import gcu.production.qr_check.android.R
import gcu.production.qrcheck.StructureApp.NetworkActions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

internal class LaunchViewModel: ViewModelRX<LaunchModel>(), NetworkActions
{
    override val liveDataState =
        MutableLiveData<LaunchModel>()
            .default(LaunchModel.LoadingState)

    override fun registrationOfInteraction(additionalData: FragmentActivity?) =
        additionalData?.let { activity ->
            this.liveDataState.observe(activity) {
                when (it) {
                    is LaunchModel.LoadingState ->
                        NetworkConnection.checkingAccessWithActions(
                            actionSuccess = ::launchWithCheckNetworkConnection,
                            actionFault =  ::networkFaultConnection,
                            listenerForFailConnection = this
                        )

                    is LaunchModel.SuccessState ->
                        actionSuccessState(
                            it.dataStorageService
                                ?: return@observe
                        )

                    else -> return@observe
                }
            }
        }

    override fun networkFaultConnection()
    {
        this.liveDataState.set(
            LaunchModel.FaultState(
                R.string.toastErrorNetwork
            )
        )
    }

    override fun launchWithCheckNetworkConnection()
    {
        this.liveDataState.set(
            LaunchModel.SwitchSuccessState
        )
    }

    private infix fun actionSuccessState(dataStorageService: DataStorageService)
    {
        Executors
            .newSingleThreadScheduledExecutor()
            .schedule(
                {
                    MainScope().launch(Dispatchers.Main)
                    {
                        liveDataState.set(
                            LaunchModel.SuccessState(
                                navigationId = if (!dataStorageService.actionWithAuth(
                                        DataStorageService.LOGIN_ID, null
                                    ).isNullOrEmpty()
                            && !dataStorageService.actionWithAuth(
                                DataStorageService.PASSWORD_ID, null
                                    ).isNullOrEmpty())
                            (if (dataStorageService.actionWithAuth(
                                    DataStorageService.ROLE_ID, null
                                ) == "USER")
                                R.id.primaryLaunchGeneralFragmentUser
                            else
                                R.id.primaryLaunchGeneralFragmentAdmin)
                        else
                            R.id.primaryLaunchAuth
                            )
                        )
                    }
                }, 50, TimeUnit.MILLISECONDS)
    }
}