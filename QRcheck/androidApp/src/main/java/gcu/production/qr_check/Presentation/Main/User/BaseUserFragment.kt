@file:Suppress("PackageName")
package gcu.production.qr_check.Presentation.Main.User

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import gcu.production.qr_check.Domain.Models.User.BaseUserModel
import gcu.production.qr_check.Domain.Models.set
import gcu.production.qr_check.Domain.ViewFactories.CustomLoadingDialogFactory
import gcu.production.qr_check.Domain.ViewFactories.SupportBaseUserViewModelFactory
import gcu.production.qrcheck.StructureApp.GeneralStructure
import gcu.production.qr_check.Presentation.CustomViews.CustomLoadingDialog
import gcu.production.qr_check.Repository.Data.MVVM.InvalidDataModel
import gcu.production.qr_check.Presentation.Main.baseUserFragmentUIActions
import gcu.production.qr_check.Presentation.Main.buildResultScanDialog
import gcu.production.qr_check.Repository.Data.MVVM.InteractionView
import gcu.production.qr_check.Repository.Data.MVVM.InteractionViewModel
import gcu.production.qr_check.android.databinding.FragmentGeneralAppUserBinding
import gcu.production.qr_check.getModuleAppComponent
import kotlinx.coroutines.*
import javax.inject.Inject

@DelicateCoroutinesApi
internal class BaseUserFragment : Fragment()
    , GeneralStructure
    , InvalidDataModel
    , InteractionView
{
    @Inject
    lateinit var animSelected: Animation
    @Inject
    lateinit var customLoadingDialogFactory: CustomLoadingDialogFactory
    @Inject
    lateinit var supportBaseUserViewModelFactory: SupportBaseUserViewModelFactory

    private lateinit var viewBinding: FragmentGeneralAppUserBinding

    private val loadingDialog: CustomLoadingDialog by lazy {
        customLoadingDialogFactory.create(requireActivity())
    }

    val viewModel: InteractionViewModel<BaseUserModel> by viewModels {
        supportBaseUserViewModelFactory create requireActivity()
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
            FragmentGeneralAppUserBinding.inflate(layoutInflater)
    }

    override fun basicBehavior()
    {
        viewBinding baseUserFragmentUIActions this
        registrationOfInteraction()

        viewModel.registrationOfInteraction(requireActivity())
        checkFragmentOrientation()
    }

    override fun registrationOfInteraction(additionalData: FragmentActivity?) =
        this.viewModel
            .liveDataState
            .observe(viewLifecycleOwner) {
                when (it) {
                    is BaseUserModel.LoadingState ->
                        this.loadingDialog.startLoadingDialog()

                    is BaseUserModel.FaultDetectorState -> {
                        this.loadingDialog.stopLoadingDialog()
                        requireContext() buildResultScanDialog false
                    }

                    is BaseUserModel.SuccessDetectorState -> {
                        this.loadingDialog.stopLoadingDialog()
                        requireContext() buildResultScanDialog true
                    }

                    is BaseUserModel.FaultDataState ->
                        this isDataInvalid requireActivity()

                    else -> return@observe
                }
            }

    private fun checkFragmentOrientation()
    {
        if (viewModel.liveDataState.value
                    is BaseUserModel.ActiveDetectorState)
            viewModel.liveDataState.set(
                BaseUserModel.ActiveDetectorState(
                    viewBinding.cameraSurfaceView)
            )
    }
}