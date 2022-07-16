@file:Suppress("PackageName")
package gcu.production.qr_check.Presentation.Main.Admin

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import gcu.production.qr_check.Domain.Models.Admin.BaseAdminModel
import gcu.production.qr_check.Domain.ViewFactories.SupportBaseFragmentFactory
import gcu.production.qr_check.Domain.ViewFactories.CustomLoadingDialogFactory
import gcu.production.qr_check.Presentation.Adapters.PointsAdapter
import gcu.production.qr_check.Presentation.Adapters.recyclerViewOptions
import gcu.production.qr_check.Repository.Data.MVVM.InvalidDataModel
import gcu.production.qrcheck.StructureApp.GeneralStructure
import gcu.production.qr_check.Presentation.Main.baseFragmentUIActions
import gcu.production.qr_check.Repository.Data.MVVM.InteractionView
import gcu.production.qr_check.Repository.Data.MVVM.InteractionViewModel
import gcu.production.qr_check.android.R
import gcu.production.qr_check.android.databinding.FragmentGeneralAppAdminBinding
import gcu.production.qr_check.getModuleAppComponent
import kotlinx.coroutines.*
import javax.inject.Inject

@DelicateCoroutinesApi
internal class BaseAdminFragment : Fragment()
    , GeneralStructure
    , InvalidDataModel
    , InteractionView
{
    @Inject
    lateinit var animSelected: Animation
    @Inject
    lateinit var viewModelFactory: SupportBaseFragmentFactory
    @Inject
    lateinit var customLoadingDialogFactory: CustomLoadingDialogFactory

    private lateinit var viewBinding: FragmentGeneralAppAdminBinding

    private val viewModel: InteractionViewModel<BaseAdminModel> by viewModels {
        this.viewModelFactory.create(requireActivity())
    }

    private val customLoadingDialog by lazy {
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
            FragmentGeneralAppAdminBinding.inflate(layoutInflater)
    }

    override fun basicBehavior()
    {
        this.viewBinding.baseFragmentUIActions(
            this
            , this.viewModel
            , this.animSelected
        )
        registrationOfInteraction()
    }

    override fun registrationOfInteraction(additionalData: FragmentActivity?) =
        this.viewModel
            .liveDataState
            .observe(viewLifecycleOwner)
            {
                when (it)
                {
                    is BaseAdminModel.SuccessfulDataForList ->
                    {
                        this.customLoadingDialog.stopLoadingDialog()
                            viewBinding.listAllPoint recyclerViewOptions PointsAdapter(
                                it.data
                                , actionClick = { args ->
                                    Navigation
                                        .findNavController(viewBinding.root)
                                    .navigate(
                                        R.id.actionLaunchListAllRecordsFragment,
                                        args
                                    )
                            }
                        )
                    }

                    is BaseAdminModel.FaultInvalidData ->
                        this@BaseAdminFragment isDataInvalid requireActivity()

                    is BaseAdminModel.LoadingDataForList
                        , is BaseAdminModel.LoadingCreatePoint ->
                        this.customLoadingDialog.startLoadingDialog()

                    is BaseAdminModel.ActionsSuccessfully ->
                        this.customLoadingDialog.stopLoadingDialog()

                    else -> return@observe
                }
            }
}