@file:Suppress("PackageName")
package gcu.production.qr_check.Presentation.Main.Common

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.preference.PreferenceFragmentCompat
import gcu.production.qr_check.Domain.Models.Common.SettingsModel
import gcu.production.qr_check.Domain.Models.default
import gcu.production.qr_check.Domain.ViewFactories.CustomLoadingDialogFactory
import gcu.production.qr_check.Domain.ViewFactories.SupportSettingsViewModelFactory
import gcu.production.qr_check.Presentation.CustomViews.CustomLoadingDialog
import gcu.production.qr_check.Presentation.Main.baseSettingsFragmentUIActions
import gcu.production.qr_check.Repository.Data.MVVM.InteractionView
import gcu.production.qr_check.Repository.Data.MVVM.InteractionViewModel
import gcu.production.qr_check.android.R
import gcu.production.qr_check.getModuleAppComponent
import gcu.production.qrcheck.StructureApp.GeneralStructure
import javax.inject.Inject

internal class SettingsFragment : PreferenceFragmentCompat()
    , GeneralStructure
    , InteractionView
{
    @Inject
    lateinit var supportSettingsViewModelFactory: SupportSettingsViewModelFactory
    @Inject
    lateinit var customLoadingDialogFactory: CustomLoadingDialogFactory

    private val viewModel: InteractionViewModel<SettingsModel> by viewModels {
        supportSettingsViewModelFactory.create(
            requireActivity()
        )
    }
    private val customLoadingDialog: CustomLoadingDialog by lazy {
        customLoadingDialogFactory.create(
            requireActivity()
        )
    }

    override fun onAttach(context: Context)
    {
        requireActivity()
            .getModuleAppComponent()
            .inject(this)
        super.onAttach(context)
    }

    override fun onCreatePreferences(
        savedInstanceState: Bundle?
        , rootKey: String?)
    {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        objectsInit()
        basicBehavior()
    }

    override fun objectsInit() =
        this baseSettingsFragmentUIActions viewModel

    override fun basicBehavior() =
        registrationOfInteraction()

    override fun registrationOfInteraction(additionalData: FragmentActivity?) =
        this.viewModel.liveDataState.observe(requireActivity())
        {
            when (it)
            {
                is SettingsModel.ThemeState ->
                    Toast.makeText(
                        requireContext(),
                        R.string.toastSuccessChangeTheme,
                        Toast.LENGTH_SHORT
                    ).show()

                is SettingsModel.RemoveAccountState ->
                    if (it.context == null)
                        requireActivity().let { activity ->
                            activity.finish()
                            activity.startActivity(
                                Intent(
                                    activity,
                                    BasicActivity::class.java
                                )
                            )
                        }

                is SettingsModel.ExitState ->
                    if (it.context == null)
                        Navigation.findNavController(
                            this.view ?: return@observe
                        ).navigate(R.id.actionExitAccount)

                is SettingsModel.LoadingState ->
                    this.customLoadingDialog.startLoadingDialog()

                is SettingsModel.MessageChangeState ->
                    Toast.makeText(
                        requireContext(),
                        it.faultMessageId,
                        Toast.LENGTH_SHORT
                    ).show()

                else -> return@observe
            }
        }

    override fun onSaveInstanceState(outState: Bundle)
    {
        super.onSaveInstanceState(outState)
        this.viewModel.liveDataState.default(
            SettingsModel.DefaultState
        )
    }
}