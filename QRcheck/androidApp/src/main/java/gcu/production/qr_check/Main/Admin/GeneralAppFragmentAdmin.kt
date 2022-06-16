package gcu.production.qr_check.Main.Admin

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.location.Location
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.widget.AppCompatEditText
import androidx.navigation.Navigation
import gcu.production.qr_check.Service.Base64.Base64Factory
import gcu.production.qrcheck.AppEngine.EngineSDK
import gcu.production.qrcheck.RestAPI.Features.RestInteraction.restAPI
import gcu.production.qrcheck.RestAPI.Models.Point.DataPointInputEntity
import gcu.production.qrcheck.RestAPI.Models.Point.DataPointOutputEntity
import gcu.production.qrcheck.StructureApp.GeneralStructure
import gcu.production.qrcheck.StructureApp.NetworkActions
import gcu.production.qrcheck.android.GeneralAppUI.CustomLoadingDialog
import gcu.production.qr_check.android.R
import gcu.production.qrcheck.android.Service.Adapters.CustomListViewAdapterPoint
import gcu.production.qr_check.Service.GeolocationListener
import gcu.production.qr_check.Service.GeolocationListener.Companion.checkPermissions
import gcu.production.qr_check.Service.DataStorageService
import gcu.production.qr_check.Service.NetworkConnection
import gcu.production.qr_check.android.databinding.FragmentGeneralAppAdminBinding
import kotlinx.coroutines.*
import java.time.LocalDateTime

@DelicateCoroutinesApi
internal class GeneralAppFragmentAdmin :
    Fragment(), GeneralStructure, NetworkActions
{
    private lateinit var viewBinding: FragmentGeneralAppAdminBinding
    private lateinit var loadingDialog: CustomLoadingDialog
    private lateinit var geolocationListener: GeolocationListener
    private lateinit var dataStorageService: DataStorageService
    private lateinit var animSelected: Animation

    private var radius: Int? = null
    private var timeActive: Long? = null

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
            FragmentGeneralAppAdminBinding.inflate(layoutInflater)

        this.dataStorageService =
            DataStorageService(requireContext())

        this.loadingDialog =
            CustomLoadingDialog(requireActivity())

        this.geolocationListener =
            GeolocationListener(
                requireActivity()
                , ::sendCreateCompletePoint)

        this.animSelected =
            AnimationUtils.loadAnimation(
                requireContext()
                , R.anim.select_object)
    }

    override fun basicBehavior()
    {
        loadDataForListView()
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(this) {
                requireActivity().finish()
            }

        this.viewBinding.btnCreatePoint.setOnClickListener {
            it.startAnimation(this.animSelected)

            NetworkConnection
                .checkingAccessWithActions(
                    actionSuccess = ::showDialogCreatePointOptions
                    , actionFault = ::networkFaultConnection
                    , actionsLoadingAfterAndBefore =  Pair(
                        Runnable { this.loadingDialog.startLoadingDialog() }
                        , Runnable { this.loadingDialog.stopLoadingDialog() })
                )
        }

        this.viewBinding.listAllPoint.setOnItemClickListener {
                adapterView, _, position, _ ->
            val bundleSendData = Bundle()
            bundleSendData.putLong(
                DATA_SELECT_KEY
                , ((adapterView.adapter.getItem(position)
                        as DataPointInputEntity).id!!)
            )

            Navigation
                .findNavController(
                    this.viewBinding.root)
                .navigate(
                    R.id.actionLaunchListAllRecordsFragment
                    , bundleSendData)
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

    private fun loadDataForListView()
    {
        this.loadingDialog.startLoadingDialog()



        val completeListData: Deferred<List<DataPointInputEntity>> =
            GlobalScope.async(Dispatchers.IO)
            {
                EngineSDK
                    .restAPI
                    .restPointRepository
                    .getAllPoint(authAction())
            }

        GlobalScope.launch(Dispatchers.Main)
        {
            try
            {
                viewBinding
                    .listAllPoint
                    .removeAllViews()
            } catch (ex: UnsupportedOperationException) {}

            viewBinding.listAllPoint.adapter =
                CustomListViewAdapterPoint(
                    requireContext()
                    , completeListData.await())

            loadingDialog.stopLoadingDialog()
        }
    }

    private fun showDialogCreatePointOptions()
    {
        val userInputDataRadius =
            AppCompatEditText(requireContext())
        val userInputDataTime =
            AppCompatEditText(requireContext())
        val layoutContainer =
            LinearLayout(requireContext())

        userInputDataRadius.hint =
            getString(R.string.infoEditTextQRCreator)
        userInputDataTime.hint =
            getString(R.string.infoEditTextQRCreator1)

        userInputDataRadius.inputType =
            InputType.TYPE_CLASS_NUMBER
        userInputDataTime.inputType =
            InputType.TYPE_CLASS_DATETIME

        userInputDataTime.gravity = Gravity.CENTER
        userInputDataRadius.gravity = Gravity.CENTER
        layoutContainer.gravity = Gravity.CENTER
        layoutContainer.orientation = LinearLayout.VERTICAL

        layoutContainer.addView(userInputDataRadius)
        layoutContainer.addView(userInputDataTime)

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.infoCreateQRTitle))
            .setMessage(getString(R.string.infoMessageQRCreator))
            .setView(layoutContainer)
            .setPositiveButton("Создать")
            {it, _ ->
                if (requireActivity().checkPermissions()
                    && !userInputDataRadius.text.isNullOrEmpty()
                    && !userInputDataTime.text.isNullOrEmpty())
                {
                   try
                   {
                       this.radius =
                           userInputDataRadius.text?.toString()?.toInt()
                       this.timeActive =
                           userInputDataTime.text?.toString()?.toLong()
                   } catch (ex: ClassCastException)
                   {
                       Toast.makeText(
                           requireContext()
                           , R.string.errorInputRadius
                           , Toast.LENGTH_SHORT)
                           .show()
                       return@setPositiveButton
                   }

                    this.loadingDialog.startLoadingDialog()
                    this.geolocationListener.launch {
                        this.loadingDialog.stopLoadingDialog()
                    }
                    it.cancel()
                }
            }
            .setNegativeButton("Отмена") { it, _ -> it.cancel() }
            .create()
            .show()
    }

    @SuppressLint("NewApi")
    private fun sendCreateCompletePoint(location: Location)
    {
        if (this.radius == null)
            return

        val sendCompleteData: Deferred<DataPointInputEntity?> =
            GlobalScope.async(Dispatchers.IO)
            {
                EngineSDK
                    .restAPI
                    .restPointRepository
                    .generatePoint(
                        authAction()
                        , DataPointOutputEntity(
                            location.longitude
                            , location.latitude
                            , this@GeneralAppFragmentAdmin.radius!!
                            , LocalDateTime
                                .now()
                                .plusDays(
                                    this@GeneralAppFragmentAdmin.timeActive ?: 31)
                                .toString()
                        )
                    )
            }

        GlobalScope.launch(Dispatchers.Main)
        {
            loadingDialog.stopLoadingDialog()

            Toast.makeText(
                requireContext()
                , if (sendCompleteData.await() != null)
                {
                    loadDataForListView()
                    R.string.successCreateQR
                }
                else
                    R.string.faultCreateQR
                , Toast.LENGTH_LONG)
                .show()
        }
    }

    override fun authAction() =
        Base64Factory
            .createEncoder()
            .encodeToString(("${dataStorageService.actionWithAuth(
                DataStorageService.LOGIN_ID, null)}" +
                    ":${dataStorageService.actionWithAuth(
                        DataStorageService.PASSWORD_ID, null)}")
                .toByteArray())

    companion object
    {
        internal const val DATA_SELECT_KEY = "selectData"
    }
}