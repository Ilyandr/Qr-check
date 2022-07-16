@file:Suppress("PackageName")
@file:OptIn(DelicateCoroutinesApi::class)

package gcu.production.qr_check.Domain.ViewModels.Admin

import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import gcu.production.qr_check.Domain.Models.Admin.BaseAdminModel
import gcu.production.qr_check.Domain.Models.default
import gcu.production.qr_check.Domain.Models.set
import gcu.production.qr_check.Repository.Data.MVVM.ViewModelRX
import gcu.production.qr_check.Service.Repository.Base64.Base64Factory
import gcu.production.qr_check.Service.Repository.DataStorageService
import gcu.production.qr_check.Service.ServiceRepository
import gcu.production.qr_check.android.R
import gcu.production.qrcheck.AppEngine.EngineSDK
import gcu.production.qrcheck.RestAPI.Features.RestInteraction.restAPI
import gcu.production.qrcheck.RestAPI.Models.Point.DataPointInputEntity
import gcu.production.qrcheck.RestAPI.Models.Point.DataPointOutputEntity
import kotlinx.coroutines.*
import java.time.LocalDateTime

internal class BaseAdminViewModel(
   contextCall: FragmentActivity
): ViewModelRX<BaseAdminModel>()
{
    private val geolocationListener by lazy {
        ServiceRepository.geolocationListener.init(contextCall)
    }

    private val dataStorageService: DataStorageService by lazy {
        ServiceRepository.dataStorageService.init(contextCall)
    }

    override val liveDataState =
        MutableLiveData<BaseAdminModel>()
            .default(BaseAdminModel.DefaultData)

    init {
        registrationOfInteraction(contextCall)
    }

    private fun loadDataForListView()
    {
        val completeListData: Deferred<List<DataPointInputEntity>?> =
            GlobalScope.async(Dispatchers.IO)
            {
                EngineSDK
                    .restAPI
                    .restPointRepository
                    .getAllPoint(authAction())
            }

        GlobalScope.launch(Dispatchers.Main)
        {
            liveDataState.set(
                completeListData.await()?.let {
                    BaseAdminModel.SuccessfulDataForList(it)
                } ?: BaseAdminModel.FaultInvalidData
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

    private fun sendCreateCompletePoint(
        X: Double
        , Y: Double
        , additionalData: Pair<Int, Long?>
    ) {
        val sendCompleteData: Deferred<DataPointInputEntity?> =
            GlobalScope.async(Dispatchers.IO)
            {
                EngineSDK
                    .restAPI
                    .restPointRepository
                    .generatePoint(
                        authAction()
                        , DataPointOutputEntity(
                            X
                            , Y
                            , additionalData.first
                            , LocalDateTime
                                .now()
                                .plusDays(
                                    additionalData.second ?: 31)
                                .toString()
                        )
                    )
            }

        GlobalScope.launch(Dispatchers.Main)
        {
            this@BaseAdminViewModel.liveDataState.set(
                if (sendCompleteData.await() != null)
                    BaseAdminModel.SuccessfulCreatePoint
                else
                    BaseAdminModel.FaultCreatePoint
            )
        }
    }

    override fun registrationOfInteraction(additionalData: FragmentActivity?) =
        additionalData?.let { activity ->
            this.liveDataState.observe(activity)
            {
                when (it)
                {
                    is BaseAdminModel.DefaultData ->
                        loadDataForListView()

                    is BaseAdminModel.LoadingCreatePoint ->
                    {
                        geolocationListener.launch(
                            additionalDataPoint = Pair(it.X, it.Y)
                            , actionForSuccessPoint = ::sendCreateCompletePoint
                            , actionForFault = {
                                liveDataState.set(
                                    BaseAdminModel.FaultCreatePoint
                                )
                            }
                        )
                    }

                    is BaseAdminModel.FaultCreatePoint ->
                    {
                        Toast.makeText(
                            activity
                            , R.string.errorCreatePoint
                            , Toast.LENGTH_LONG
                        ).show()

                        liveDataState.set(
                            BaseAdminModel.ActionsSuccessfully
                        )
                    }

                    is BaseAdminModel.SuccessfulCreatePoint ->
                    {
                        Toast.makeText(
                            activity
                            , R.string.successCreateQR
                            , Toast.LENGTH_SHORT
                        ).show()

                        loadDataForListView()
                        liveDataState.set(
                            BaseAdminModel.ActionsSuccessfully
                        )
                    }

                    else -> return@observe
                }
            }
        }
}