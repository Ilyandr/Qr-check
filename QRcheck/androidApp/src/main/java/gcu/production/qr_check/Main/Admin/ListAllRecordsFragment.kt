package gcu.production.qrcheck.android.Main.Admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.navigation.Navigation
import gcu.production.qr_check.Service.Base64.Base64Factory
import gcu.production.qrcheck.AppEngine.EngineSDK
import gcu.production.qrcheck.RestAPI.Features.RestInteraction.restAPI
import gcu.production.qrcheck.RestAPI.Models.User.UserInputEntity
import gcu.production.qrcheck.StructureApp.GeneralStructure
import gcu.production.qrcheck.StructureApp.NetworkActions
import gcu.production.qrcheck.android.GeneralAppUI.CustomLoadingDialog
import gcu.production.qrcheck.android.Main.Admin.GeneralAppFragmentAdmin.Companion.DATA_SELECT_KEY
import gcu.production.qr_check.android.R
import gcu.production.qrcheck.android.Service.Adapters.CustomListViewAdapterRecord
import gcu.production.qrcheck.android.Service.NetworkConnection
import gcu.production.qr_check.Service.DataStorageService
import gcu.production.qr_check.android.databinding.FragmentListAllRecordsBinding
import kotlinx.coroutines.*

@DelicateCoroutinesApi
internal class ListAllRecordsFragment
    : Fragment(), GeneralStructure, NetworkActions
{
    private lateinit var viewBinding: FragmentListAllRecordsBinding
    private lateinit var loadingDialog: CustomLoadingDialog
    private lateinit var dataStorageService: DataStorageService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View
    {
        objectsInit()
        launchWithCheckNetworkConnection()
        return this.viewBinding.root
    }

    override fun objectsInit()
    {
        this.viewBinding =
            FragmentListAllRecordsBinding.inflate(layoutInflater)

        this.dataStorageService =
            DataStorageService(requireContext())

        this.loadingDialog =
            CustomLoadingDialog(requireActivity())
    }

    override fun basicBehavior()
    {
        inflateListView()

        this.viewBinding.btnShowQR.setOnClickListener {
            it.startAnimation(
                AnimationUtils.loadAnimation(
                    requireContext()
                    , R.anim.select_object)
            )

            val bundleSendData = Bundle()

            bundleSendData.putLong(
                DATA_SELECT_KEY
                , requireArguments().getLong(DATA_SELECT_KEY)
            )

            Navigation
                .findNavController(
                    this.viewBinding.root)
                .navigate(
                    R.id.actionLaunchFragmentShowQR
                    , bundleSendData)
        }
    }

    private fun inflateListView()
    {
        this.loadingDialog.startLoadingDialog()

        val dataInflateForListRecord: Deferred<List<UserInputEntity>> =
            GlobalScope.async(Dispatchers.IO)
            {
                EngineSDK.restAPI.
                restRecordRepository
                    .getAllRecord(
                        authAction()
                        , requireArguments().getLong(DATA_SELECT_KEY))
            }

        GlobalScope.launch(Dispatchers.Main)
        {
            this@ListAllRecordsFragment
                .viewBinding
                .listAllRecord
                .adapter = CustomListViewAdapterRecord(
                requireContext()
                , dataInflateForListRecord.await())

            loadingDialog.stopLoadingDialog()
        }
    }

    override fun launchWithCheckNetworkConnection() =
        NetworkConnection
            .checkingAccessWithActions(
                actionSuccess = ::basicBehavior
                , actionFault = ::networkFaultConnection
                , actionsLoadingAfterAndBefore =  Pair(
                    Runnable { this.loadingDialog.startLoadingDialog() }
                    , Runnable { this.loadingDialog.stopLoadingDialog() })
                ,  listenerForFailConnection = this
            )

    override fun networkFaultConnection() =
        Toast.makeText(
            requireContext()
            ,R.string.toastMessageFaultConnection
            , Toast.LENGTH_SHORT)
            .show()

    override fun authAction() =
        Base64Factory
            .createEncoder()
            .encodeToString(("${dataStorageService.actionWithAuth(
                DataStorageService.LOGIN_ID)}" +
                    ":${dataStorageService.actionWithAuth(
                        DataStorageService.PASSWORD_ID)}")
                .toByteArray())
}