package gcu.production.qrcheck.android.Main.User

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import gcu.production.qrcheck.AppEngine.EngineSDK
import gcu.production.qrcheck.RestAPI.Features.RestInteraction.restAPI
import gcu.production.qrcheck.StructureApp.GeneralStructure
import gcu.production.qrcheck.android.Authorization.Base64Encoder.encodeAuthDataToBase64Key
import gcu.production.qrcheck.android.GeneralAppUI.CustomLoadingDialog
import gcu.production.qrcheck.android.R
import gcu.production.qrcheck.android.Service.Detectors.BarcodeDetectorService
import gcu.production.qrcheck.android.Service.GeolocationListener
import gcu.production.qrcheck.android.Service.GeolocationListener.Companion.checkPermissions
import gcu.production.qrcheck.android.Service.SharedPreferencesAuth
import gcu.production.qrcheck.android.databinding.FragmentGeneralAppUserBinding
import kotlinx.coroutines.*

@DelicateCoroutinesApi
internal class GeneralAppFragmentUser : Fragment(), GeneralStructure
{
    private lateinit var viewBinding: FragmentGeneralAppUserBinding
    private lateinit var barcodeDetectorService: BarcodeDetectorService
    private lateinit var geolocationListener: GeolocationListener
    private lateinit var loadingDialog: CustomLoadingDialog
    private lateinit var animSelected: Animation
    private var barcodeCompleteInfo: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View
    {
        objectsInit()
        basicBehavior()
        return this.viewBinding.root
    }

    override fun objectsInit()
    {
        this.viewBinding =
            FragmentGeneralAppUserBinding.inflate(layoutInflater)

        this.loadingDialog =
            CustomLoadingDialog(requireActivity())

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
                        SharedPreferencesAuth(
                            requireContext()
                        ).encodeAuthDataToBase64Key()
                        , this@GeneralAppFragmentUser.barcodeCompleteInfo
                        , Pair(location.latitude, location.longitude)
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

    override fun onDestroy()
    {
        super.onDestroy()
        stopStream()
    }
}