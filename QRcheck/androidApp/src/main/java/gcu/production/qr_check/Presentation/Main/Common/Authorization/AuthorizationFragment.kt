@file:Suppress("PackageName")
package gcu.production.qr_check.Presentation.Main.Common.Authorization

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import gcu.production.qr_check.Domain.Models.Common.Authorization.CommonAuthorizationModel
import gcu.production.qr_check.Domain.Models.set
import gcu.production.qr_check.Domain.ViewFactories.CustomLoadingDialogFactory
import gcu.production.qr_check.Domain.ViewModels.Common.Authorization.AuthorizationViewModel
import gcu.production.qr_check.android.databinding.FragmentAuthorizationBinding
import gcu.production.qrcheck.StructureApp.GeneralStructure
import gcu.production.qr_check.Presentation.CustomViews.CustomLoadingDialog
import gcu.production.qr_check.Presentation.Main.authorizationFragmentUIActions
import gcu.production.qr_check.Repository.Data.MVVM.InteractionView
import gcu.production.qr_check.Repository.Data.MVVM.InteractionViewModel
import gcu.production.qr_check.getModuleAppComponent
import javax.inject.Inject

internal class AuthorizationFragment : Fragment(), GeneralStructure, InteractionView
{
    @Inject
    lateinit var customLoadingDialog: CustomLoadingDialogFactory
    @Inject
    lateinit var animSelected: Animation

    private lateinit var viewBinding: FragmentAuthorizationBinding

    private val viewModel: InteractionViewModel<CommonAuthorizationModel>
        by viewModels<AuthorizationViewModel>()

    private val progressDialog: CustomLoadingDialog by lazy {
        customLoadingDialog.create(requireActivity())
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
            FragmentAuthorizationBinding.inflate(layoutInflater)

        viewBinding.authorizationFragmentUIActions(
            this, this.viewModel
        )
    }

    override fun basicBehavior()
    {
        registrationOfInteraction()
        viewModel.registrationOfInteraction(requireActivity())
    }

    override fun registrationOfInteraction(additionalData: FragmentActivity?) =
        this.viewModel.liveDataState.observe(viewLifecycleOwner)
        {
            when (it)
            {
                is CommonAuthorizationModel.LoadingStateAuth ->
                    this.progressDialog.startLoadingDialog()

                is CommonAuthorizationModel.FaultState ->
                {
                    this.progressDialog.stopLoadingDialog()
                    Toast.makeText(
                        requireContext(),
                        it.messageId,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is CommonAuthorizationModel.SuccessState ->
                {
                    this.progressDialog.stopLoadingDialog()
                    Navigation
                        .findNavController(viewBinding.root)
                        .navigate(it.navigateId, it.bundleData)
                }

                else -> return@observe
            }
        }

    override fun onPause()
    {
        super.onPause()
        this.viewModel.liveDataState.set(
            CommonAuthorizationModel.DefaultState
        )
    }

    companion object {
        internal const val LOGIN_KEY = "inputLogin"
    }
}