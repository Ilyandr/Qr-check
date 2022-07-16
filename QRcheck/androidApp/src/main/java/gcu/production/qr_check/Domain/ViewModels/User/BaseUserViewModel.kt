@file:Suppress("PackageName")
package gcu.production.qr_check.Domain.ViewModels.User

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import gcu.production.qr_check.Domain.Models.User.BaseUserModel
import gcu.production.qr_check.Domain.Models.default
import gcu.production.qr_check.Domain.Models.set
import gcu.production.qr_check.Domain.ViewFactories.BarcodeDetectorServiceFactory
import gcu.production.qr_check.Repository.Data.MVVM.ViewModelRX
import gcu.production.qr_check.Repository.Features.Detectors.BarcodeDetectorService
import gcu.production.qr_check.Service.Repository.Base64.Base64Factory
import gcu.production.qr_check.Service.Repository.DataStorageService
import gcu.production.qr_check.Service.Repository.GeolocationListener
import gcu.production.qr_check.Service.ServiceRepository
import gcu.production.qr_check.getModuleAppComponent
import gcu.production.qrcheck.AppEngine.EngineSDK
import gcu.production.qrcheck.RestAPI.Features.RestInteraction.restAPI
import kotlinx.coroutines.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.inject.Inject

internal class BaseUserViewModel(
    contextCall: FragmentActivity
): ViewModelRX<BaseUserModel>()
{
    @Inject
    lateinit var barcodeDetectorServiceFactory: BarcodeDetectorServiceFactory

    override val liveDataState =
        MutableLiveData<BaseUserModel>()
            .default(BaseUserModel.DefaultState)

    private val dataStorageService: DataStorageService by lazy {
        ServiceRepository.dataStorageService.init(contextCall)
    }

    private val barcodeDetectorService: BarcodeDetectorService by lazy {
        this.barcodeDetectorServiceFactory create ::stageFirstUpdateBarcode
    }

    private val geolocationListener: GeolocationListener by lazy {
        ServiceRepository.geolocationListener.init(contextCall)
    }

    init {
        registrationOfInteraction(contextCall)
        contextCall.getModuleAppComponent().inject(this)
    }

    override fun registrationOfInteraction(additionalData: FragmentActivity?) =
        additionalData?.let {
            this.liveDataState.observe(it)
            { viewModel ->
                when (viewModel)
                {
                    is BaseUserModel.ActiveDetectorState ->
                        Executors
                            .newSingleThreadScheduledExecutor()
                            .schedule({
                                MainScope().launch(Dispatchers.Main) {
                                    barcodeDetectorService.launchBarcodeDetector(
                                        viewModel.surface
                                    )
                                } }, 50, TimeUnit.MILLISECONDS
                            )

                    else -> return@observe
                }
            }
        }

    private fun stageFirstUpdateBarcode(newValue: String)
    {
        stopStream()
        this.liveDataState.set(
            BaseUserModel.LoadingState
        )

        this.geolocationListener.launch(
            additionalDataScan = newValue
            , actionForSuccessScan = ::stageSecondSendCompleteData
            , actionForFault = {
                this.liveDataState.set(
                    BaseUserModel.FaultDetectorState
                )
            }
        )
    }

    private fun stageSecondSendCompleteData(X: Double, Y: Double, barcodeData: String)
    {
        val sendBarcodeResult: Deferred<Long?> =
            MainScope().async(Dispatchers.IO)
            {
                EngineSDK
                    .restAPI
                    .restRecordRepository
                    .setRecord(
                        authAction()
                        , barcodeData
                        , Pair(Y, X)
                    )
            }

        MainScope().launch(Dispatchers.Main)
        {
            liveDataState.set(
                if (sendBarcodeResult.await() != null)
                    BaseUserModel.SuccessDetectorState
                else
                    BaseUserModel.FaultDetectorState
            )
        }
    }

    private fun stopStream() = try
    {
        this.barcodeDetectorService
            .surfaceView
            .holder
            .removeCallback(this.barcodeDetectorService)

        this.barcodeDetectorService
            .surfaceDestroyed(
                this.barcodeDetectorService
                    .surfaceView
                    .holder
            )
    } catch (ignored: Exception) { }

    private fun authAction() =
        Base64Factory
            .createEncoder()
            .encodeToString(("${dataStorageService.actionWithAuth(
                DataStorageService.LOGIN_ID, null)}" +
                    ":${dataStorageService.actionWithAuth(
                        DataStorageService.PASSWORD_ID, null)}")
                .toByteArray())
}