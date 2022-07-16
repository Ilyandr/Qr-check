@file:Suppress("PackageName")
package gcu.production.qr_check.Domain.ViewModels.Common.Authorization

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import gcu.production.qr_check.Domain.Models.Common.Authorization.RegistrationModel
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
import gcu.production.qrcheck.RestAPI.Models.User.UserOutputEntity
import kotlinx.coroutines.*

internal class RegistrationViewModel: ViewModelRX<RegistrationModel>()
{
    override val liveDataState =
        MutableLiveData<RegistrationModel>()
            .default(RegistrationModel.DefaultState)

    private lateinit var dataStorageService: DataStorageService

    override fun registrationOfInteraction(additionalData: FragmentActivity?) =
        additionalData?.let { activity ->
            if (!::dataStorageService.isInitialized)
                this.dataStorageService =
                    ServiceRepository.dataStorageService.init(activity)

            this.liveDataState.observe(activity) {
                when (it) {
                    is RegistrationModel.LoadingStageFirstState ->
                        DataCorrectness.checkInputUserData(
                            selectedAction = DataCorrectness.PASSWORD_ACTION
                            , inputUserData = arrayOf(it.passwordData)
                            , actionForSuccess = ::successResultCheckPassword
                            , actionForFault = ::showFaultResultCheckData)

                    is RegistrationModel.LoadingStageSecondState ->
                        DataCorrectness.checkInputUserData(
                            selectedAction = DataCorrectness.COMMON_ACTION_REGISTER
                            , inputUserData = arrayOf(
                                it.allRegisterData?.password
                                , it.allRegisterData?.name
                                , it.allRegisterData?.jobTitle
                                , it.allRegisterData?.organization
                                , it.allRegisterData?.roles?.get(0))
                            , actionForSuccess = ::successResultCheckAllData
                            , actionForFault = ::showFaultResultCheckData)


                    else -> return@observe
                }
            }
        }

    private fun successResultCheckPassword(correctPassword: List<String>)
    {
        this.liveDataState.set(
            RegistrationModel.LoadingSwitchingState(
                correctPassword[0]
            )
        )
    }

    private fun successResultCheckAllData(inputCorrectData: List<String>)
    {
        if (!::dataStorageService.isInitialized)
        {
            showFaultResultCheckData()
            return
        }

        val sendRegisterData: Deferred<UserInputEntity?> =
            MainScope().async(Dispatchers.IO)
            {
                EngineSDK
                    .restAPI
                    .restAuthRepository
                    .registration(
                        UserOutputEntity(
                            name = inputCorrectData[1]
                            , password = inputCorrectData[0]
                            , phone = (liveDataState.value
                                    as RegistrationModel.LoadingStageSecondState)
                                .allRegisterData
                            !!.phone
                            , jobTitle = inputCorrectData[2]
                            , organization = inputCorrectData[3]
                            , roles = listOf(inputCorrectData[4])
                        )
                    )
            }

        MainScope().launch(Dispatchers.Main)
        {
            sendRegisterData.await()?.let {

                dataStorageService.actionWithAuth(
                    DataStorageService.PASSWORD_ID
                    , (liveDataState.value
                            as RegistrationModel.LoadingStageSecondState)
                        .allRegisterData
                    !!.password
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
                    RegistrationModel.SuccessState(
                        if (it.roles?.get(0) == "USER")
                            R.id.actionLaunchGeneralAppFragmentUser
                        else
                            R.id.actionLaunchGeneralAppFragmentAdmin
                    )
                )
            } ?: showFaultResultCheckData()
        }
    }

    private fun showFaultResultCheckData() =
        this.liveDataState.set(
            RegistrationModel.FaultState(
                R.string.toastErrorCommonCheckData
            )
        )
}