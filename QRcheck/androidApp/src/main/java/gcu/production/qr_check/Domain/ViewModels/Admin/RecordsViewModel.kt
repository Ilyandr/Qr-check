@file:Suppress("PackageName")
package gcu.production.qr_check.Domain.ViewModels.Admin

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import gcu.production.qr_check.Domain.Models.Admin.RecordsModel
import gcu.production.qr_check.Domain.Models.default
import gcu.production.qr_check.Domain.Models.set
import gcu.production.qr_check.Repository.Data.MVVM.ViewModelRX
import gcu.production.qr_check.Service.Repository.Base64.Base64Factory
import gcu.production.qr_check.Service.Repository.DataStorageService
import gcu.production.qr_check.Service.ServiceRepository
import gcu.production.qrcheck.AppEngine.EngineSDK
import gcu.production.qrcheck.RestAPI.Features.RestInteraction.restAPI
import gcu.production.qrcheck.RestAPI.Models.User.UserInputEntity
import kotlinx.coroutines.*

internal class RecordsViewModel(
    contextCall: FragmentActivity
): ViewModelRX<RecordsModel>()
{
    override val liveDataState =
        MutableLiveData<RecordsModel>()
            .default(RecordsModel.DefaultState)

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
                    is RecordsModel.LoadingState ->
                        this inflateListView it.selectId

                    else -> return@observe
                }
            }
        }

    private infix fun inflateListView(selectId: Long)
    {
        val dataInflateForListRecord: Deferred<List<UserInputEntity>?> =
            MainScope().async(Dispatchers.IO)
            {
                EngineSDK.restAPI.
                restRecordRepository
                    .getAllRecord(
                        authAction()
                        , selectId
                    )
            }

        MainScope().launch(Dispatchers.Main)
        {
            dataInflateForListRecord.await()?.let {
                liveDataState.set(
                    RecordsModel.SuccessfulState(it)
                )
            } ?: liveDataState.set(
                RecordsModel.FaultState
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