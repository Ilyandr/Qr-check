@file:Suppress("PackageName")
package gcu.production.qr_check.Main.User

import android.content.res.Configuration
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import gcu.production.qr_check.GeneralAppUI.ActionBarSettings.hideBar
import gcu.production.qr_check.GeneralAppUI.ActionBarSettings.setBarOptions
import gcu.production.qr_check.Service.Base64.Base64Factory
import gcu.production.qrcheck.AppEngine.EngineSDK
import gcu.production.qrcheck.RestAPI.Features.RestInteraction.restAPI
import gcu.production.qrcheck.StructureApp.GeneralStructure
import gcu.production.qrcheck.StructureApp.NetworkActions
import gcu.production.qrcheck.android.GeneralAppUI.CustomLoadingDialog
import gcu.production.qr_check.android.R
import gcu.production.qrcheck.android.Service.Detectors.BarcodeDetectorService
import gcu.production.qr_check.Service.GeolocationListener
import gcu.production.qr_check.Service.GeolocationListener.Companion.checkPermissions
import gcu.production.qr_check.Service.DataStorageService
import gcu.production.qr_check.Service.NetworkConnection
import gcu.production.qr_check.android.databinding.FragmentGeneralAppUserBinding
import kotlinx.coroutines.*

@DelicateCoroutinesApi
internal class GeneralAppFragmentUser
    : Fragment(), GeneralStructure, NetworkActions
{
    private lateinit var viewBinding: FragmentGeneralAppUserBinding
    private lateinit var barcodeDetectorService: BarcodeDetectorService
    private lateinit var geolocationListener: GeolocationListener
    private lateinit var dataStorageService: DataStorageService
    private lateinit var loadingDialog: CustomLoadingDialog
    private lateinit var animSelected: Animation
    private var barcodeCompleteInfo: String? = null

    override fun onCreateView(
        inflater: LayoutInflater
        , container: ViewGroup?
        , savedInstanceState: Bundle?): View
    {
        objectsInit()
        launchWithCheckNetworkConnection()
        return this.viewBinding.root
    }

    override fun objectsInit()
    {
        this.viewBinding =
            FragmentGeneralAppUserBinding.inflate(layoutInflater)

        this.loadingDialog =
            CustomLoadingDialog(requireActivity())

        this.dataStorageService =
            DataStorageService(requireContext())

        this.animSelected =
            AnimationUtils.loadAnimation(
                requireContext()
                , R.anim.select_object)

        this.geolocationListener =
            GeolocationListener(
                requireContext()
                , ::stageSecondSendCompleteData)

        this.barcodeDetectorService =
            BarcodeDetectorService(
                this.viewBinding.cameraSurfaceView
                , ::stageFirstUpdateBarcode)

        if (requireActivity()
                .resources
                .configuration
                .orientation ==
            Configuration.ORIENTATION_LANDSCAPE)
                requireActivity().hideBar()
        else
            requireActivity().setBarOptions(
                R.string.titleFragmentGeneralUser
                , false
            )
    }

    override fun basicBehavior()
    {
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(this) {
                requireActivity().finish()
            }

        this.viewBinding.btnScanQR.setOnClickListener {
            it.startAnimation(this.animSelected)

            if (requireActivity().checkPermissions())
            {
                viewBinding.cameraSurfaceView.background = null
                this.barcodeDetectorService
                    .launchBarcodeDetector()
            }
        }

        this.viewBinding.btnSettings.setOnClickListener {
            it.startAnimation(this.animSelected)

            Navigation
                .findNavController(this.viewBinding.root)
                .navigate(R.id.actionLaunchSettingsFragment)
        }
    }

    private fun stageFirstUpdateBarcode(newValue: String)
    {
        stopStream()
        this.loadingDialog.startLoadingDialog()
        this.barcodeCompleteInfo = newValue

        this.geolocationListener.launch {
            this.loadingDialog.stopLoadingDialog()
        }
    }

    private fun stageSecondSendCompleteData(location: Location)
    {
        val sendBarcodeResult: Deferred<Long?> =
            GlobalScope.async(Dispatchers.IO)
            {
                EngineSDK
                    .restAPI
                    .restRecordRepository
                    .setRecord(
                        authAction()
                        , this@GeneralAppFragmentUser.barcodeCompleteInfo
                        , Pair(location.longitude, location.latitude)
                    )
            }

        GlobalScope.launch(Dispatchers.Main)
        {
            this@GeneralAppFragmentUser
                .loadingDialog
                .stopLoadingDialog()

                AlertDialog.Builder(requireContext())
                    .setTitle(R.string.infoAlertDialog)
                    .setMessage(
                        if (sendBarcodeResult.await() != null)
                            R.string.successQRScan
                        else
                            R.string.faultQRScan)
                    .create()
                    .show()
        }
    }

    private fun stopStream() = try
    {
        this.viewBinding.cameraSurfaceView.holder
            .removeCallback(this.barcodeDetectorService)
        this.barcodeDetectorService
            .surfaceDestroyed(viewBinding.cameraSurfaceView.holder)
    } catch (ignored: Exception) { }

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
                DataStorageService.LOGIN_ID, null)}" +
                    ":${dataStorageService.actionWithAuth(
                        DataStorageService.PASSWORD_ID, null)}")
                .toByteArray())

    override fun onDestroy()
    {
        super.onDestroy()
        stopStream()
    }
}