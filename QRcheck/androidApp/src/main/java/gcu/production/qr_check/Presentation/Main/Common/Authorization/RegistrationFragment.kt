@file:Suppress("PackageName")
package gcu.production.qr_check.Presentation.Main.Common.Authorization

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import gcu.production.qr_check.Domain.Models.Common.Authorization.RegistrationModel
import gcu.production.qr_check.Domain.Models.set
import gcu.production.qr_check.Domain.ViewFactories.CustomLoadingDialogFactory
import gcu.production.qr_check.Domain.ViewModels.Common.Authorization.RegistrationViewModel
import gcu.production.qrcheck.RestAPI.Models.User.UserOutputEntity
import gcu.production.qrcheck.StructureApp.GeneralStructure
import gcu.production.qr_check.Presentation.Main.Common.Authorization.AuthorizationFragment.Companion.LOGIN_KEY
import gcu.production.qr_check.Presentation.CustomViews.CustomLoadingDialog
import gcu.production.qr_check.Repository.Data.MVVM.InteractionView
import gcu.production.qr_check.Repository.Data.MVVM.InteractionViewModel
import gcu.production.qr_check.Presentation.CustomViews.PopupMenuCreator
import gcu.production.qr_check.Presentation.Main.registrationFragmentUIActions
import gcu.production.qr_check.android.databinding.FragmentRegistrationBinding
import gcu.production.qr_check.getModuleAppComponent
import javax.inject.Inject

internal class RegistrationFragment : Fragment()
    , GeneralStructure
    , InteractionView
{
    @Inject
    lateinit var animSelected: Animation
    @Inject
    lateinit var customLoadingDialogFactory: CustomLoadingDialogFactory
    @Inject
    lateinit var popupMenuCreator: PopupMenuCreator

    private lateinit var viewBinding: FragmentRegistrationBinding

    private val viewModel: InteractionViewModel<RegistrationModel>
            by viewModels<RegistrationViewModel>()

    private val loadingDialog: CustomLoadingDialog by lazy {
        customLoadingDialogFactory.create(requireActivity())
    }

    override fun onAttach(context: Context)
    {
        requireActivity()
            .getModuleAppComponent()
            .inject(this)
        super.onAttach(context)
    }

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

        viewBinding.registrationFragmentUIActions(
            this, this.viewModel
        )
    }

    override fun basicBehavior()
    {
        registrationOfInteraction()
        this.viewModel.registrationOfInteraction(requireActivity())
    }

    override fun registrationOfInteraction(additionalData: FragmentActivity?) =
        this.viewModel.liveDataState.observe(viewLifecycleOwner)
        {
            when (it)
            {
                is RegistrationModel.LoadingStageFirstState ->
                    this.loadingDialog.startLoadingDialog()

                is RegistrationModel.LoadingSwitchingState ->
                    this.viewModel.liveDataState.set(
                        RegistrationModel.LoadingStageSecondState(
                            UserOutputEntity(
                                name = viewBinding.inputName.text.toString(),
                                password = it.passwordData,
                                phone = requireArguments().getString(LOGIN_KEY)!!,
                                jobTitle = viewBinding.inputJobTitle.text.toString(),
                                organization = viewBinding.inputOrganization.text.toString(),
                                roles = listOf(popupMenuCreator.selectRoleData)
                            )
                        )
                    )

                is RegistrationModel.SuccessState ->
                {
                    this.loadingDialog.stopLoadingDialog()
                    Navigation
                        .findNavController(viewBinding.root)
                        .navigate(it.navigateId)
                }

                is RegistrationModel.FaultState ->
                {
                    this.loadingDialog.stopLoadingDialog()
                    Toast.makeText(
                        requireContext(),
                        it.messageId,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> return@observe
            }
        }
}