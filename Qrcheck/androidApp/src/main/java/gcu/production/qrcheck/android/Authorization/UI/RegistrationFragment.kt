package gcu.production.qrcheck.android.Authorization.UI

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.navigation.Navigation
import gcu.production.qrcheck.AppEngine.EngineSDK
import gcu.production.qrcheck.RestAPI.Features.RestInteraction.restAPI
import gcu.production.qrcheck.RestAPI.Models.User.UserInputEntity
import gcu.production.qrcheck.RestAPI.Models.User.UserOutputEntity
import gcu.production.qrcheck.Service.DataCorrectness
import gcu.production.qrcheck.StructureApp.GeneralStructure
import gcu.production.qrcheck.android.Service.SharedPreferencesAuth
import gcu.production.qrcheck.android.Service.SharedPreferencesAuth.Companion.LOGIN_ID
import gcu.production.qrcheck.android.Service.SharedPreferencesAuth.Companion.PASSWORD_ID
import gcu.production.qrcheck.android.Authorization.UI.AuthorizationFragment.Companion.LOGIN_KEY
import gcu.production.qrcheck.android.GeneralAppUI.CustomLoadingDialog
import gcu.production.qrcheck.android.GeneralAppUI.PopupMenuCreator
import gcu.production.qrcheck.android.R
import gcu.production.qrcheck.android.databinding.FragmentRegistrationBinding
import kotlinx.coroutines.*

@DelicateCoroutinesApi
internal class RegistrationFragment : Fragment(), GeneralStructure
{
    private lateinit var viewBinding: FragmentRegistrationBinding
    private lateinit var animSelected: Animation
    private lateinit var loadingDialog: CustomLoadingDialog
    private lateinit var userRole: String

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
        this.viewBinding =
            FragmentRegistrationBinding.inflate(layoutInflater)
        this.loadingDialog =
            CustomLoadingDialog(requireActivity())
        this.animSelected =
            AnimationUtils.loadAnimation(
                requireContext()
                , R.anim.select_object)
    }

    override fun basicBehavior()
    {
        PopupMenuCreator.createMenuForSelectUserRole(
            this.viewBinding.btnSelectRole
            , this.animSelected
            , ::setUserRole)

        this.viewBinding
            .btnNextStage
            .setOnClickListener {
                it.startAnimation(animSelected)

                DataCorrectness.checkInputUserData(
                    selectedAction = DataCorrectness.PASSWORD_ACTION
                    , inputUserData = arrayOf(
                        this.viewBinding.inputPassword.text.toString())
                    , actionForSuccess = ::successResultCheckPassword
                    , actionForFault = ::showFaultResultCheckData)
            }
    }

    private fun showFaultResultCheckData() =
        Toast.makeText(
            requireContext()
            , R.string.toastErrorCommonCheckData
            , Toast.LENGTH_SHORT)
            .show()

    private fun successResultCheckPassword(unused: List<String>)
    {
        DataCorrectness.checkInputUserData(
            selectedAction = DataCorrectness.COMMON_ACTION_REGISTER
            , inputUserData = arrayOf(
                unused[0]
                , this.viewBinding.inputName.text.toString()
                , this.viewBinding.inputJobTitle.text.toString()
                , this.viewBinding.inputOrganization.text.toString()
                , this.userRole)
            , actionForSuccess = ::successResultCheckAllData
            , actionForFault = ::showFaultResultCheckData)
    }

    private fun successResultCheckAllData(inputCorrectData: List<String>)
    {
        this.loadingDialog.startLoadingDialog()

        val sendRegisterData: Deferred<UserInputEntity?> =
            GlobalScope.async(Dispatchers.IO)
            {
                EngineSDK
                    .restAPI
                    .restAuthRepository
                    .registration(
                        UserOutputEntity(
                            name = inputCorrectData[1]
                            , password = inputCorrectData[0]
                            , phone = requireArguments().getString(LOGIN_KEY)!!
                            , jobTitle = inputCorrectData[2]
                            , organization = inputCorrectData[3]
                            , roles = listOf(inputCorrectData[4])
                        )
                    )
            }

        GlobalScope.launch(Dispatchers.Main)
        {
            sendRegisterData.await()?.let {
                val sharedPreferencesAuth =
                    SharedPreferencesAuth(requireActivity())

                sharedPreferencesAuth.actionWithAuth(
                    PASSWORD_ID
                    , viewBinding.inputPassword.text.toString())
                sharedPreferencesAuth.actionWithAuth(
                    LOGIN_ID
                    , it.phone)
                sharedPreferencesAuth.actionWithAuth(
                    SharedPreferencesAuth.ROLE_ID
                    , it.roles?.get(0))

                Navigation
                    .findNavController(viewBinding.root)
                    .navigate(
                        if (it.roles?.get(0) == getString(R.string.roleID0))
                            R.id.actionLaunchGeneralAppFragmentUser
                        else
                            R.id.actionLaunchGeneralAppFragmentAdmin)
            } ?: showFaultResultCheckData()

            loadingDialog.stopLoadingDialog()
        }
    }

    private fun setUserRole(newUserRole: String)
    {
        this.viewBinding.registerRoleInfo.text =
            getString(
                if (newUserRole == getString(R.string.roleID0))
                    R.string.infoPopupMenuRole0
                else
                    R.string.infoPopupMenuRole1)
        this.userRole = newUserRole
    }
}