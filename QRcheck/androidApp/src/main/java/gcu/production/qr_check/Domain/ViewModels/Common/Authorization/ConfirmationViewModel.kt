@file:Suppress("PackageName")
package gcu.production.qr_check.Domain.ViewModels.Common.Authorization

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import gcu.production.qr_check.Domain.Models.Common.Authorization.CommonAuthorizationModel
import gcu.production.qr_check.Domain.Models.default
import gcu.production.qr_check.Domain.Models.set
import gcu.production.qr_check.Repository.Data.MVVM.ViewModelRX
import gcu.production.qr_check.Service.Repository.DataCorrectness
import gcu.production.qr_check.Service.Repository.DataStorageService
import gcu.production.qr_check.Service.ServiceRepository
import gcu.production.qr_check.android.R
import gcu.production.qrcheck.AppEngine.EngineSDK
import gcu.production.qrcheck.RestAPI.Features.RestInteraction.restAPI
import gcu.production.qrcheck.RestAPI.Models.User.UserInputEntity
import kotlinx.coroutines.*
import java.nio.charset.StandardCharsets
import java.util.*

internal class ConfirmationViewModel: ViewModelRX<CommonAuthorizationModel>()
{
    override val liveDataState =
        MutableLiveData<CommonAuthorizationModel>()
            .default(CommonAuthorizationModel.DefaultState)

    private lateinit var dataStorageService: DataStorageService

    override fun registrationOfInteraction(additionalData: FragmentActivity?) =
        additionalData?.let {
            if (!::dataStorageService.isInitialized)
                dataStorageService =
                    ServiceRepository.dataStorageService.init(it)

            this.liveDataState.observe(it) { stateNow ->
                when (stateNow) {
                    is CommonAuthorizationModel.LoadingStateConfirm ->
                        DataCorrectness.checkInputUserData(
                            selectedAction = DataCorrectness.PASSWORD_ACTION
                            , inputUserData =  arrayOf(stateNow.passwordData)
                            , actionForFault = ::errorCheckPassword
                            , actionForSuccess = ::successCheckPassword
                        )

                    else -> return@observe
                }
            }
        }

    private fun successCheckPassword(newCorrectValue: List<String>)
    {
        if (!::dataStorageService.isInitialized)
        {
            errorCheckPassword()
            return
        }

        val sendAuthData: Deferred<UserInputEntity?> =
            MainScope().async(Dispatchers.IO)
            {
                EngineSDK
                    .restAPI
                    .restAuthRepository
                    .login(Base64.getEncoder().encodeToString(
                        ((liveDataState.value
                                as CommonAuthorizationModel.LoadingStateConfirm
                                ).loginData + ":${newCorrectValue[0]}")
                            .toByteArray(StandardCharsets.UTF_8)
                      )
                  )
            }

        MainScope().launch(Dispatchers.Main)
        {
            sendAuthData.await()?.let {

                dataStorageService.actionWithAuth(
                    DataStorageService.PASSWORD_ID
                    , newCorrectValue[0]
                )
                dataStorageService.actionWithAuth(
                    DataStorageService.LOGIN_ID
                    , it.phone
                )
                dataStorageService.actionWithAuth(
                    DataStorageService.ROLE_ID
                    , it.roles?.get(0)
                )

                liveDataState.set(
                    CommonAuthorizationModel.SuccessState(
                        if (it.roles?.get(0) == "USER")
                            R.id.actionLaunchGeneralAppFragmentUser
                        else
                            R.id.actionLaunchGeneralAppFragmentAdmin
                    )
                )
            } ?: errorCheckPassword()
        }
    }

    private fun errorCheckPassword() =
        this.liveDataState.set(
            CommonAuthorizationModel.FaultState(
                R.string.toastErrorPassword
            )
        )
}