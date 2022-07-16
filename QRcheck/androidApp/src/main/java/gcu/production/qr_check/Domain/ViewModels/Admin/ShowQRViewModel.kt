@file:Suppress("PackageName")
package gcu.production.qr_check.Domain.ViewModels.Admin

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import gcu.production.qr_check.Domain.Models.Admin.ShowQRModel
import gcu.production.qr_check.Domain.Models.default
import gcu.production.qr_check.Domain.Models.set
import gcu.production.qr_check.Repository.Data.MVVM.ViewModelRX
import gcu.production.qr_check.Service.Repository.Base64.Base64Factory
import gcu.production.qr_check.Service.Repository.DataStorageService
import gcu.production.qr_check.Service.ServiceRepository
import gcu.production.qrcheck.AppEngine.EngineSDK
import gcu.production.qrcheck.RestAPI.Features.RestInteraction.restAPI
import kotlinx.coroutines.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

internal class ShowQRViewModel(
    contextCall: FragmentActivity
): ViewModelRX<ShowQRModel>()
{
    override val liveDataState =
        MutableLiveData<ShowQRModel>()
            .default(ShowQRModel.DefaultState)

    private val dataStorageService: DataStorageService by lazy {
        ServiceRepository.dataStorageService.init(contextCall)
    }

    init {
        registrationOfInteraction(contextCall)
    }

    override fun registrationOfInteraction(additionalData: FragmentActivity?) =
        additionalData?.let { activity ->
            this.liveDataState.observe(activity)
            {
                when (it)
                {
                    is ShowQRModel.LoadingState ->
                        this registerExecutorUpdate it.selectKeyId

                    else -> return@observe
                }
            }
        }

    private infix fun registerExecutorUpdate(selectKeyId: Long) =
        Executors
            .newSingleThreadScheduledExecutor()
            .scheduleAtFixedRate(
                { this generateBarcodeImage selectKeyId }
                , 0
                , 60
                , TimeUnit.SECONDS
            )

    private infix fun generateBarcodeImage(selectKeyId: Long)
    {
        val generateToken: Deferred<String?> =
            MainScope().async(Dispatchers.IO)
            {
                EngineSDK
                    .restAPI
                    .restPointRepository
                    .generateToken(
                        authAction()
                        , selectKeyId
                    )
            }

        MainScope().launch(Dispatchers.Main)
        {
            generateToken.await()?.let {
                liveDataState.set(
                    ShowQRModel.SuccessState(it)
                )
            } ?: liveDataState.set(
                ShowQRModel.FaultState
            )
        }
    }

    private fun authAction() =
        Base64Factory
            .createEncoder()
            .encodeToString(("${dataStorageService.actionWithAuth(
                DataStorageService.LOGIN_ID, null)}" +
                    ":${dataStorageService.actionWithAuth(
                        DataStorageService.PASSWORD_ID, null)}")
                .toByteArray())
}