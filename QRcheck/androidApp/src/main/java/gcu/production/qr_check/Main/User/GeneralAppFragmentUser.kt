package gcu.production.qrcheck.android.Main.User

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
import gcu.production.qr_check.Service.Base64.Base64Factory
import gcu.production.qrcheck.AppEngine.EngineSDK
import gcu.production.qrcheck.RestAPI.Features.RestInteraction.restAPI
import gcu.production.qrcheck.StructureApp.GeneralStructure
import gcu.production.qrcheck.StructureApp.NetworkActions
import gcu.production.qrcheck.android.GeneralAppUI.CustomLoadingDialog
import gcu.production.qr_check.android.R
import gcu.production.qrcheck.android.Service.Detectors.BarcodeDetectorService
import gcu.production.qrcheck.android.Service.GeolocationListener
import gcu.production.qrcheck.android.Service.GeolocationListener.Companion.checkPermissions
import gcu.production.qrcheck.android.Service.NetworkConnection
import gcu.production.qrcheck.android.Service.SharedPreferencesAuth
import gcu.production.qr_check.android.databinding.FragmentGeneralAppUserBinding
import kotlinx.coroutines.*

@DelicateCoroutinesApi
internal class GeneralAppFragmentUser
    : Fragment(), GeneralStructure, NetworkActions
{
    private lateinit var viewBinding: FragmentGeneralAppUserBinding
    private lateinit var barcodeDetectorService: BarcodeDetectorService
    private lateinit var geolocationListener: GeolocationListener
    private lateinit var sharedPreferencesAuth: SharedPreferencesAuth
    private lateinit var loadingDialog: CustomLoadingDialog
    private lateinit var animSelected: Animation
    private var barcodeCompleteInfo: String? = null

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
            FragmentGeneralAppUserBinding.inflate(layoutInflater)

        this.loadingDialog =
            CustomLoadingDialog(requireActivity())

        this.sharedPreferencesAuth =
            SharedPreferencesAuth(requireContext())

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
                this.barcodeDetectorService
                    .launchBarcodeDetector()
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
            .encodeToString(("${sharedPreferencesAuth.actionWithAuth(
                SharedPreferencesAuth.LOGIN_ID)}" +
                    ":${sharedPreferencesAuth.actionWithAuth(
                        SharedPreferencesAuth.PASSWORD_ID)}")
                .toByteArray())

    override fun onDestroy()
    {
        super.onDestroy()
        stopStream()
    }
}