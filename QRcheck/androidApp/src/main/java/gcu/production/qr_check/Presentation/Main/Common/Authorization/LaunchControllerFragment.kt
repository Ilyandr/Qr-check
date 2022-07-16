@file:Suppress("PackageName")
package gcu.production.qr_check.Presentation.Main.Common.Authorization

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import gcu.production.qr_check.Domain.Models.Common.Authorization.LaunchModel
import gcu.production.qr_check.Domain.Models.set
import gcu.production.qr_check.Domain.ViewFactories.CustomLoadingDialogFactory
import gcu.production.qr_check.Domain.ViewModels.Common.Authorization.LaunchViewModel
import gcu.production.qr_check.Presentation.CustomViews.ActionBarSettings.setBarOptions
import gcu.production.qr_check.Presentation.CustomViews.CustomLoadingDialog
import gcu.production.qr_check.Repository.Data.MVVM.InteractionView
import gcu.production.qr_check.Repository.Data.MVVM.InteractionViewModel
import gcu.production.qr_check.android.databinding.FragmentLaunchControllerBinding
import gcu.production.qrcheck.StructureApp.GeneralStructure
import gcu.production.qr_check.Service.Repository.DataStorageService
import gcu.production.qr_check.Service.ServiceRepository
import gcu.production.qr_check.getModuleAppComponent
import javax.inject.Inject

internal class LaunchControllerFragment : Fragment()
    , GeneralStructure
    , InteractionView
{
    @Inject
    lateinit var customLoadingDialogFactory: CustomLoadingDialogFactory

    private lateinit var bindingView: FragmentLaunchControllerBinding

    private val viewModel: InteractionViewModel<LaunchModel>
        by viewModels<LaunchViewModel>()

    private val customLoadingDialog: CustomLoadingDialog by lazy {
        customLoadingDialogFactory.create(requireActivity())
    }

    private val dataStorageService: DataStorageService by lazy {
        ServiceRepository.dataStorageService.init(requireContext())
    }

    override fun onAttach(context: Context)
    {
        requireActivity()
            .getModuleAppComponent()
            .inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View
    {
        objectsInit()
        basicBehavior()
        return bindingView.root
    }

    override fun objectsInit()
    {
        this.bindingView =
            FragmentLaunchControllerBinding.inflate(layoutInflater)

        requireActivity()
            .setBarOptions(backButtonActive = false)
    }

    override fun basicBehavior()
    {
        registrationOfInteraction()
        this.viewModel.registrationOfInteraction(
            requireActivity()
        )
    }

    override fun registrationOfInteraction(additionalData: FragmentActivity?) =
        this.viewModel.liveDataState.observe(viewLifecycleOwner)
        {
            when (it)
            {
                is LaunchModel.LoadingState ->
                    this.customLoadingDialog.startLoadingDialog()

                is LaunchModel.FaultState ->
                    Toast.makeText(
                        requireContext(),
                        it.messageId,
                        Toast.LENGTH_SHORT
                    ).show()

                is LaunchModel.SwitchSuccessState ->
                    viewModel.liveDataState.set(
                        LaunchModel.SuccessState(
                            dataStorageService = this.dataStorageService
                        )
                    )

                is LaunchModel.SuccessState ->
                {
                    if (it.dataStorageService == null)
                    {
                        this.customLoadingDialog.stopLoadingDialog()
                        Navigation
                            .findNavController(bindingView.root)
                            .navigate(it.navigationId!!)
                    }
                }
            }
        }
}