package gcu.production.qrcheck.android.Main.Admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import gcu.production.qrcheck.AppEngine.EngineSDK
import gcu.production.qrcheck.RestAPI.Features.RestInteraction.restAPI
import gcu.production.qrcheck.StructureApp.GeneralStructure
import gcu.production.qrcheck.StructureApp.NetworkActions
import gcu.production.qrcheck.android.Authorization.Base64Encoder.encodeAuthDataToBase64Key
import gcu.production.qrcheck.android.GeneralAppUI.CustomLoadingDialog
import gcu.production.qrcheck.android.Main.Admin.GeneralAppFragmentAdmin.Companion.DATA_SELECT_KEY
import gcu.production.qr_check.android.R
import gcu.production.qrcheck.android.Service.BarcodeGenerator.setCardBarcode
import gcu.production.qrcheck.android.Service.NetworkConnection
import gcu.production.qrcheck.android.Service.SharedPreferencesAuth
import gcu.production.qr_check.android.databinding.FragmentShowQRBinding
import kotlinx.coroutines.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@DelicateCoroutinesApi
internal class FragmentShowQR
    : Fragment(), GeneralStructure, NetworkActions
{
    private lateinit var viewBinding: FragmentShowQRBinding
    private lateinit var loadingDialog: CustomLoadingDialog
    private lateinit var basicAnimation: Animation

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
            FragmentShowQRBinding.inflate(layoutInflater)

        this.loadingDialog =
            CustomLoadingDialog(requireActivity())

        this.basicAnimation =
            AnimationUtils.loadAnimation(
                requireContext()
                , R.anim.select_object)
    }

    override fun basicBehavior()
    {
        Executors
            .newSingleThreadScheduledExecutor()
            .scheduleAtFixedRate(
                {
                    if (!NetworkConnection.isActiveConnectionListener)
                        NetworkConnection
                            .checkingAccessWithActions(
                                actionSuccess = ::generateBarcodeImage
                                , actionFault = ::networkFaultConnection
                                , actionsLoadingAfterAndBefore =  Pair(
                                    Runnable { this.loadingDialog.startLoadingDialog() }
                                    , Runnable { this.loadingDialog.stopLoadingDialog() })
                                ,  listenerForFailConnection = this
                            )
                }
                , 0
                , 1
                , TimeUnit.MINUTES
            )

        this.viewBinding.btnBack.setOnClickListener {
            it.startAnimation(this.basicAnimation)
            requireActivity().onBackPressed()
        }
    }

    private fun generateBarcodeImage()
    {
        val generateToken: Deferred<String?> =
            GlobalScope.async(Dispatchers.IO)
            {
                EngineSDK
                    .restAPI
                    .restPointRepository
                    .generateToken(
                        SharedPreferencesAuth(
                            requireContext()).encodeAuthDataToBase64Key()
                        , requireArguments().getLong(DATA_SELECT_KEY)
                    )
            }

        GlobalScope.launch(Dispatchers.Main)
        {
            this@FragmentShowQR
                .viewBinding
                .progressLoading
                .scaleX = 0f

            this@FragmentShowQR
                .viewBinding
                .showBarcodeImageView
                .setImageBitmap(
                    Pair(1920, 1080) setCardBarcode generateToken.await())

            if (generateToken.await() == null)
                generateBarcodeImage()

            this@FragmentShowQR.viewBinding.statusTextView.text =
                getString(R.string.infoScanAdminQR)
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
}