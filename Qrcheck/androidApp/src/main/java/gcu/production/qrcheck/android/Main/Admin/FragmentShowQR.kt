package gcu.production.qrcheck.android.Main.Admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import gcu.production.qrcheck.AppEngine.EngineSDK
import gcu.production.qrcheck.RestAPI.Features.RestInteraction.restAPI
import gcu.production.qrcheck.StructureApp.GeneralStructure
import gcu.production.qrcheck.android.Authorization.Base64Encoder.encodeAuthDataToBase64Key
import gcu.production.qrcheck.android.Main.Admin.GeneralAppFragmentAdmin.Companion.DATA_SELECT_KEY
import gcu.production.qrcheck.android.R
import gcu.production.qrcheck.android.Service.BarcodeGenerator.setCardBarcode
import gcu.production.qrcheck.android.Service.SharedPreferencesAuth
import gcu.production.qrcheck.android.databinding.FragmentShowQRBinding
import kotlinx.coroutines.*

@DelicateCoroutinesApi
internal class FragmentShowQR : Fragment(), GeneralStructure
{
    private lateinit var viewBinding: FragmentShowQRBinding
    private lateinit var basicAnimation: Animation

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
            FragmentShowQRBinding.inflate(layoutInflater)
        this.basicAnimation =
            AnimationUtils.loadAnimation(
                requireContext()
                , R.anim.select_object)
    }

    override fun basicBehavior()
    {
        generateBarcodeImage()

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

            this@FragmentShowQR.viewBinding.statusTextView.text =
                getString(R.string.infoScanAdminQR)
        }
    }
}