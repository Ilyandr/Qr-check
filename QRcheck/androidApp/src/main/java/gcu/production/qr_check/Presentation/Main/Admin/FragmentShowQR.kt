@file:Suppress("PackageName")
package gcu.production.qr_check.Presentation.Main.Admin

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import gcu.production.qr_check.Domain.Models.Admin.ShowQRModel
import gcu.production.qr_check.Domain.ViewFactories.CustomLoadingDialogFactory
import gcu.production.qr_check.Domain.ViewFactories.SupportShowQRVMFactory
import gcu.production.qr_check.Domain.Models.set
import gcu.production.qr_check.Repository.Data.MVVM.InvalidDataModel
import gcu.production.qrcheck.StructureApp.GeneralStructure
import gcu.production.qr_check.Presentation.CustomViews.CustomLoadingDialog
import gcu.production.qr_check.Presentation.Main.Common.BasicActivity.Companion.DATA_SELECT_KEY
import gcu.production.qr_check.Presentation.Main.fragmentShowQRUIActions
import gcu.production.qr_check.Presentation.Main.fragmentUpdateQRUIActions
import gcu.production.qr_check.Repository.Data.MVVM.InteractionView
import gcu.production.qr_check.Repository.Data.MVVM.InteractionViewModel
import gcu.production.qr_check.android.databinding.FragmentShowQRBinding
import gcu.production.qr_check.getModuleAppComponent
import javax.inject.Inject

internal class FragmentShowQR : Fragment()
    , GeneralStructure
    , InteractionView
    , InvalidDataModel
{
    @Inject
    lateinit var customLoadingDialogFactory: CustomLoadingDialogFactory
    @Inject
    lateinit var viewModelFactory: SupportShowQRVMFactory

    private lateinit var viewBinding: FragmentShowQRBinding

    private val viewModel: InteractionViewModel<ShowQRModel> by viewModels {
        this.viewModelFactory.create(requireActivity())
    }
    private val customLoadingDialog: CustomLoadingDialog by lazy {
        this.customLoadingDialogFactory.create(requireActivity())
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
        return this.viewBinding.root
    }

    override fun objectsInit()
    {
        this.viewBinding =
            FragmentShowQRBinding.inflate(layoutInflater)
    }

    override fun basicBehavior()
    {
        viewBinding fragmentShowQRUIActions this
        registrationOfInteraction()
    }

    override fun registrationOfInteraction(additionalData: FragmentActivity?) =
        this.viewModel
            .liveDataState
            .observe(viewLifecycleOwner)
            {
                when(it)
                {
                    is ShowQRModel.DefaultState ->
                    {
                        this.customLoadingDialog.startLoadingDialog()
                        viewModel.liveDataState.set(
                            ShowQRModel.LoadingState(
                                requireArguments().getLong(DATA_SELECT_KEY)
                            )
                        )
                    }

                    is ShowQRModel.SuccessState ->
                    {
                        this.customLoadingDialog.stopLoadingDialog()
                        viewBinding fragmentUpdateQRUIActions it.barcodeData
                    }

                    is ShowQRModel.FaultState ->
                        this isDataInvalid requireActivity()

                    else -> return@observe
                }
            }
}