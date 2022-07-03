@file:Suppress("PackageName")
package gcu.production.qr_check.Authorization.UI

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import gcu.production.qr_check.GeneralAppUI.ActionBarSettings.setBarOptions
import gcu.production.qr_check.android.R
import gcu.production.qr_check.android.databinding.FragmentAuthorizationBinding
import gcu.production.qrcheck.AppEngine.EngineSDK
import gcu.production.qrcheck.RestAPI.Features.RestInteraction.restAPI
import gcu.production.qrcheck.Service.DataCorrectness
import gcu.production.qrcheck.StructureApp.GeneralStructure
import gcu.production.qrcheck.android.GeneralAppUI.CustomLoadingDialog
import kotlinx.coroutines.*

@DelicateCoroutinesApi
internal class AuthorizationFragment : Fragment(), GeneralStructure
{
    private lateinit var viewBinding: FragmentAuthorizationBinding
    private lateinit var progressDialog: CustomLoadingDialog
    private lateinit var animSelected: Animation

    override fun onCreateView(
        inflater: LayoutInflater
        , container: ViewGroup?
        , savedInstanceState: Bundle?): View
    {
        objectsInit()
        basicBehavior()
        return viewBinding.root
    }

    override fun objectsInit()
    {
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(this) {
                requireActivity().finish()
            }

        this.viewBinding =
            FragmentAuthorizationBinding.inflate(layoutInflater)

        this.progressDialog =
            CustomLoadingDialog(requireActivity())

        this.animSelected =
            AnimationUtils.loadAnimation(
                requireContext()
                , R.anim.select_object)

        requireActivity()
            .setBarOptions(backButtonActive = false)
    }

    override fun basicBehavior()
    {
        this.viewBinding.btnNextStage.setOnClickListener {
            it.startAnimation(animSelected)

            DataCorrectness.checkInputUserData(
                selectedAction = DataCorrectness.LOGIN_ACTION
                , inputUserData = arrayOf(this.viewBinding.registerLogin.text.toString())
                , additionalData = "8"
                , actionForSuccess = ::successCheckLogin
                , actionForFault = ::faultCheckLogin)
        }
    }

    private fun successCheckLogin(newCorrectValue: List<String>)
    {
        this.progressDialog.startLoadingDialog()

        val checkExistUserData: Deferred<Boolean?> =
            GlobalScope.async(Dispatchers.IO)
            {
                EngineSDK
                    .restAPI
                    .restAuthRepository
                    .existUser(newCorrectValue[0])
            }

        GlobalScope.launch(Dispatchers.Main)
        {
            val sendFirstAuthData = Bundle()
            sendFirstAuthData.putString(LOGIN_KEY, newCorrectValue[0])
            progressDialog.stopLoadingDialog()

            checkExistUserData.await().let {
                Navigation
                    .findNavController(viewBinding.root)
                    .navigate(
                        if (it != null && it)
                            R.id.actionLaunchConfirmationFragment
                        else
                            R.id.actionLaunchRegistrationFragment
                        , sendFirstAuthData
                    )
            }

        }.start()
    }

    private fun faultCheckLogin() =
        Toast.makeText(
            requireContext()
            , R.string.toastErrorCheckLogin
            , Toast.LENGTH_SHORT)
            .show()

    companion object
    {
        internal const val LOGIN_KEY = "inputLogin"
    }
}