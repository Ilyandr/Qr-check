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
import gcu.production.qr_check.Domain.Models.Common.Authorization.CommonAuthorizationModel
import gcu.production.qr_check.Domain.ViewFactories.CustomLoadingDialogFactory
import gcu.production.qr_check.Domain.ViewModels.Common.Authorization.ConfirmationViewModel
import gcu.production.qr_check.android.databinding.FragmentConfirmationBinding
import gcu.production.qrcheck.StructureApp.GeneralStructure
import gcu.production.qr_check.Presentation.CustomViews.CustomLoadingDialog
import gcu.production.qr_check.Presentation.Main.confirmationFragmentUIActions
import gcu.production.qr_check.Repository.Data.MVVM.InteractionView
import gcu.production.qr_check.Repository.Data.MVVM.InteractionViewModel
import gcu.production.qr_check.getModuleAppComponent
import kotlinx.coroutines.*
import javax.inject.Inject

@DelicateCoroutinesApi
internal class ConfirmationFragment : Fragment()
    , GeneralStructure
    , InteractionView
{
    @Inject
    lateinit var animSelected: Animation
    @Inject
    lateinit var customLoadingDialogFactory: CustomLoadingDialogFactory

    private lateinit var viewBinding: FragmentConfirmationBinding

    private val viewModel: InteractionViewModel<CommonAuthorizationModel>
        by viewModels<ConfirmationViewModel>()

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
        return this.viewBinding.root
    }

    override fun objectsInit()
    {
        this.viewBinding =
            FragmentConfirmationBinding.inflate(layoutInflater)

        viewBinding.confirmationFragmentUIActions(
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
                is CommonAuthorizationModel.LoadingStateConfirm ->
                    this.loadingDialog.startLoadingDialog()

                is CommonAuthorizationModel.FaultState ->
                {
                    this.loadingDialog.stopLoadingDialog()
                    Toast.makeText(
                        requireContext(),
                        it.messageId,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is CommonAuthorizationModel.SuccessState ->
                {
                    this.loadingDialog.stopLoadingDialog()
                    Navigation
                        .findNavController(viewBinding.root)
                        .navigate(it.navigateId)
                }

                else -> return@observe
            }
        }
}