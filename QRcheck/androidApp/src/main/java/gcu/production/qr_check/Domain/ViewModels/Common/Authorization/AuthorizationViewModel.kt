@file:Suppress("PackageName")
package gcu.production.qr_check.Domain.ViewModels.Common.Authorization

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import gcu.production.qr_check.Domain.Models.Common.Authorization.CommonAuthorizationModel
import gcu.production.qr_check.Domain.Models.default
import gcu.production.qr_check.Domain.Models.set
import gcu.production.qr_check.Presentation.Main.Common.Authorization.AuthorizationFragment
import gcu.production.qr_check.Repository.Data.MVVM.ViewModelRX
import gcu.production.qr_check.Service.Repository.DataCorrectness
import gcu.production.qr_check.android.R
import gcu.production.qrcheck.AppEngine.EngineSDK
import gcu.production.qrcheck.RestAPI.Features.RestInteraction.restAPI
import kotlinx.coroutines.*

internal class AuthorizationViewModel
    : ViewModelRX<CommonAuthorizationModel>()
{
    override val liveDataState =
        MutableLiveData<CommonAuthorizationModel>()
            .default(CommonAuthorizationModel.DefaultState)

    override fun registrationOfInteraction(additionalData: FragmentActivity?) =
        additionalData?.let {
            this.liveDataState.observe(it) { stateNow ->
                when (stateNow) {
                    is CommonAuthorizationModel.LoadingStateAuth ->
                        DataCorrectness.checkInputUserData(
                            selectedAction = DataCorrectness.LOGIN_ACTION
                            , inputUserData = arrayOf(stateNow.loginData)
                            , additionalData = "8"
                            , actionForSuccess = ::successCheckLogin
                            , actionForFault = ::faultCheckLogin)

                    else -> return@observe
                }
            }
        }

    private fun successCheckLogin(newCorrectValue: List<String>)
    {
        val checkExistUserData: Deferred<Boolean?> =
            MainScope().async(Dispatchers.IO)
            {
                EngineSDK
                    .restAPI
                    .restAuthRepository
                    .existUser(newCorrectValue[0])
            }

        MainScope().launch(Dispatchers.Main)
        {
            val sendFirstAuthData = Bundle()
            sendFirstAuthData.putString(
                AuthorizationFragment.LOGIN_KEY
                , newCorrectValue[0]
            )

            checkExistUserData.await().let {
                liveDataState.set(
                    CommonAuthorizationModel.SuccessState(
                        if (it != null && it)
                            R.id.actionLaunchConfirmationFragment
                        else
                            R.id.actionLaunchRegistrationFragment
                        , sendFirstAuthData
                    )
                )
            }
        }
    }

    private fun faultCheckLogin() =
        this.liveDataState.set(
            CommonAuthorizationModel.FaultState(
                R.string.toastErrorCheckLogin
            )
        )
}