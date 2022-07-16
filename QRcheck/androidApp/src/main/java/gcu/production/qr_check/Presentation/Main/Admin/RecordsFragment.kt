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
import gcu.production.qr_check.Domain.Models.Admin.RecordsModel
import gcu.production.qr_check.Domain.Models.set
import gcu.production.qr_check.Domain.ViewFactories.CustomLoadingDialogFactory
import gcu.production.qr_check.Domain.ViewFactories.SupportRecordsFragmentFactory
import gcu.production.qr_check.Presentation.Adapters.PointsAdapter
import gcu.production.qr_check.Presentation.Adapters.recyclerViewOptions
import gcu.production.qr_check.Repository.Data.MVVM.InvalidDataModel
import gcu.production.qrcheck.StructureApp.GeneralStructure
import gcu.production.qr_check.Presentation.Main.Common.BasicActivity.Companion.DATA_SELECT_KEY
import gcu.production.qr_check.Presentation.Main.baseFragmentUIActions
import gcu.production.qr_check.Repository.Data.MVVM.InteractionView
import gcu.production.qr_check.Repository.Data.MVVM.InteractionViewModel
import gcu.production.qr_check.android.databinding.FragmentListAllRecordsBinding
import gcu.production.qr_check.getModuleAppComponent
import kotlinx.coroutines.*
import javax.inject.Inject

@DelicateCoroutinesApi
internal class RecordsFragment : Fragment()
    , GeneralStructure
    , InvalidDataModel
    , InteractionView
{
    @Inject
    lateinit var animSelected: Animation
    @Inject
    lateinit var customLoadingDialogFactory: CustomLoadingDialogFactory
    @Inject
    lateinit var supportRecordsFragmentFactory: SupportRecordsFragmentFactory

    private lateinit var viewBinding: FragmentListAllRecordsBinding

    private val customLoadingDialog by lazy {
        this.customLoadingDialogFactory.create(requireActivity())
    }

    private val viewModel: InteractionViewModel<RecordsModel> by viewModels {
        this.supportRecordsFragmentFactory.create(requireActivity())
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
            FragmentListAllRecordsBinding.inflate(layoutInflater)
    }

    override fun basicBehavior()
    {
        registrationOfInteraction()
        viewBinding baseFragmentUIActions this
    }

    override fun registrationOfInteraction(additionalData: FragmentActivity?) =
        this.viewModel
            .liveDataState
            .observe(viewLifecycleOwner)
            {
                when (it)
                {
                    is RecordsModel.SuccessfulState ->
                    {
                        viewBinding.listAllRecord recyclerViewOptions
                                PointsAdapter(it.dataList)
                        customLoadingDialog.stopLoadingDialog()
                    }

                    is RecordsModel.DefaultState ->
                    {
                        viewModel.liveDataState.set(
                            RecordsModel.LoadingState(
                                requireArguments().getLong(DATA_SELECT_KEY)
                            )
                        )
                        customLoadingDialog.startLoadingDialog()
                    }

                    is RecordsModel.FaultState ->
                        this isDataInvalid requireActivity()

                    else -> return@observe
                }
            }
}