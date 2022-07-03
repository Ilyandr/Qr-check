@file:Suppress("PackageName")
package gcu.production.qr_check.Authorization.UI

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.navigation.Navigation
import gcu.production.qr_check.android.databinding.FragmentConfirmationBinding
import gcu.production.qrcheck.AppEngine.EngineSDK
import gcu.production.qrcheck.RestAPI.Features.RestInteraction.restAPI
import gcu.production.qrcheck.RestAPI.Models.User.UserInputEntity
import gcu.production.qrcheck.Service.DataCorrectness
import gcu.production.qrcheck.StructureApp.GeneralStructure
import gcu.production.qr_check.Service.DataStorageService
import gcu.production.qr_check.Authorization.UI.AuthorizationFragment.Companion.LOGIN_KEY
import gcu.production.qr_check.GeneralAppUI.ActionBarSettings.setBarOptions
import gcu.production.qrcheck.android.GeneralAppUI.CustomLoadingDialog
import gcu.production.qr_check.android.R
import kotlinx.coroutines.*
import java.nio.charset.StandardCharsets
import java.util.*

@DelicateCoroutinesApi
internal class ConfirmationFragment : Fragment(), GeneralStructure
{
    private lateinit var viewBinding: FragmentConfirmationBinding
    private lateinit var animSelected: Animation
    private lateinit var loadingDialog: CustomLoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater
        , container: ViewGroup?
        , savedInstanceState: Bundle?): View
    {
        objectsInit()
        basicBehavior()
        return this.viewBinding.root
    }

    override fun objectsInit()
    {
        this.viewBinding =
            FragmentConfirmationBinding.inflate(layoutInflater)

        this.loadingDialog =
            CustomLoadingDialog(requireActivity())

        this.animSelected =
            AnimationUtils.loadAnimation(
                requireContext()
                , R.anim.select_object)

        requireActivity()
            .setBarOptions(backButtonActive = true)
    }

    override fun basicBehavior()
    {
        this.viewBinding
            .btnNextStage
            .setOnClickListener {
                it.startAnimation(this.animSelected)

                DataCorrectness.checkInputUserData(
                    selectedAction = DataCorrectness.PASSWORD_ACTION
                    , inputUserData =  arrayOf(
                        this.viewBinding.inputPassword.text.toString())
                    , actionForFault = ::errorCheckPassword
                    , actionForSuccess = ::successCheckPassword)
            }
    }

    private fun errorCheckPassword() =
        Toast.makeText(
            requireContext()
            , R.string.toastErrorPassword
            , Toast.LENGTH_SHORT)
            .show()

    private fun successCheckPassword(inputCorrectData: List<String>)
    {
        this.loadingDialog.startLoadingDialog()

        val sendAuthData: Deferred<UserInputEntity?> =
            GlobalScope.async(Dispatchers.IO)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    EngineSDK
                        .restAPI
                        .restAuthRepository
                        .login(
                            Base64.getEncoder().encodeToString(
                                ("${requireArguments().getString(LOGIN_KEY)}" +
                                        ":${inputCorrectData[0]}")
                                    .toByteArray(StandardCharsets.UTF_8)))
                else null
            }

        GlobalScope.launch(Dispatchers.Main)
        {
            sendAuthData.await()?.let {
                val dataStorageService =
                    DataStorageService(requireActivity())

                dataStorageService.actionWithAuth(
                    DataStorageService.PASSWORD_ID
                    , viewBinding.inputPassword.text.toString())
                dataStorageService.actionWithAuth(
                    DataStorageService.LOGIN_ID
                    , it.phone)
                dataStorageService.actionWithAuth(
                    DataStorageService.ROLE_ID
                    , it.roles?.get(0))

                Navigation
                    .findNavController(viewBinding.root)
                    .navigate(
                        if (it.roles?.get(0) == getString(R.string.roleID0))
                            R.id.actionLaunchGeneralAppFragmentUser
                        else
                            R.id.actionLaunchGeneralAppFragmentAdmin)
            } ?: errorCheckPassword()

            loadingDialog.stopLoadingDialog()
        }
    }
}