@file:Suppress("PackageName")
@file:OptIn(DelicateCoroutinesApi::class)
package gcu.production.qr_check.Presentation.Main

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.animation.Animation
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.navigation.Navigation
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import gcu.production.qr_check.Domain.Models.Admin.BaseAdminModel
import gcu.production.qr_check.Domain.Models.Common.Authorization.CommonAuthorizationModel
import gcu.production.qr_check.Domain.Models.Common.Authorization.RegistrationModel
import gcu.production.qr_check.Domain.Models.Common.SettingsModel
import gcu.production.qr_check.Domain.Models.User.BaseUserModel
import gcu.production.qr_check.Domain.Models.set
import gcu.production.qr_check.Presentation.Adapters.PointsAdapter
import gcu.production.qr_check.Presentation.Adapters.recyclerViewOptions
import gcu.production.qr_check.Presentation.CustomViews.ActionBarSettings.hideBar
import gcu.production.qr_check.Presentation.CustomViews.ActionBarSettings.setBarOptions
import gcu.production.qr_check.Presentation.CustomViews.CreatePointDialog.buildDialogCreatePoint
import gcu.production.qr_check.Presentation.Main.Admin.BaseAdminFragment
import gcu.production.qr_check.Presentation.Main.Admin.FragmentShowQR
import gcu.production.qr_check.Presentation.Main.Admin.RecordsFragment
import gcu.production.qr_check.Presentation.Main.Common.Authorization.AuthorizationFragment
import gcu.production.qr_check.Presentation.Main.Common.Authorization.AuthorizationFragment.Companion.LOGIN_KEY
import gcu.production.qr_check.Presentation.Main.Common.Authorization.ConfirmationFragment
import gcu.production.qr_check.Presentation.Main.Common.Authorization.RegistrationFragment
import gcu.production.qr_check.Presentation.Main.Common.BasicActivity.Companion.DATA_SELECT_KEY
import gcu.production.qr_check.Presentation.Main.Common.SettingsFragment
import gcu.production.qr_check.Presentation.Main.User.BaseUserFragment
import gcu.production.qr_check.Repository.Data.MVVM.InteractionViewModel
import gcu.production.qr_check.Repository.Features.Detectors.BarcodeDetectorService.Companion.checkCameraPermissions
import gcu.production.qr_check.Service.Repository.BarcodeGenerator.setCardBarcode
import gcu.production.qr_check.Service.Repository.GeolocationListener.Companion.checkLocationAvailability
import gcu.production.qr_check.android.R
import gcu.production.qr_check.android.databinding.*
import gcu.production.qrcheck.RestAPI.Models.User.UserInputEntity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
internal fun FragmentGeneralAppAdminBinding.baseFragmentUIActions(
    activity: BaseAdminFragment
    , viewModule: InteractionViewModel<BaseAdminModel>
    , animSelected: Animation)
{
    activity.requireActivity().onBackPressedDispatcher
        .addCallback(activity) {
            activity.requireActivity().finish()
        }
    this.listAllPoint recyclerViewOptions
            PointsAdapter<UserInputEntity>(emptyList())

    this.btnCreatePoint.setOnClickListener {
        it.startAnimation(animSelected)

        if (activity.requireActivity().checkLocationAvailability())
        {
            { additionalData: Pair<Int, Long?> ->
                viewModule.liveDataState.set(
                    BaseAdminModel.LoadingCreatePoint(
                        additionalData.first
                        , additionalData.second
                    )
                )
            }.buildDialogCreatePoint(
                activity.requireActivity()
            ).show()
        }
    }

    this.settings.setOnClickListener {
        it.startAnimation(animSelected)
        Navigation
            .findNavController(this.root)
            .navigate(
                R.id.actionLaunchSettingsFragment
            )
    }

    activity.requireActivity().setBarOptions(
        R.string.infoNameFragmentGeneralAdmin
        , false
    )
}

 internal infix fun FragmentShowQRBinding
         .fragmentShowQRUIActions(fragmentActivity: FragmentShowQR) =
     fragmentActivity.requireActivity().setBarOptions(
         R.string.titleFragmentShowQR
         , true
     )

infix fun FragmentShowQRBinding
        .fragmentUpdateQRUIActions(barcodeData: String) =
    MainScope().launch(Dispatchers.Main)
    {
        this@fragmentUpdateQRUIActions
            .progressLoading
            .scaleX = 0f

        this@fragmentUpdateQRUIActions
            .showBarcodeImageView
            .setImageBitmap(
                Pair(1920, 1080) setCardBarcode barcodeData
            )

        this@fragmentUpdateQRUIActions.statusTextView.text =
            this@fragmentUpdateQRUIActions
                    .statusTextView
                    .context
                    .getString(
                        R.string.infoScanAdminQR
                    )
    }

internal infix fun FragmentListAllRecordsBinding
        .baseFragmentUIActions(recordsFragment: RecordsFragment)
{
    recordsFragment.requireActivity().setBarOptions(
        R.string.infoNameFragmentAllRecords
        , true
    )

    listAllRecord recyclerViewOptions
            PointsAdapter<UserInputEntity>(emptyList())

    this.btnShowQR.setOnClickListener {
        it.startAnimation(
            recordsFragment.animSelected
        )

        val bundleSendData = Bundle()

        bundleSendData.putLong(
            DATA_SELECT_KEY
            , recordsFragment
                .requireArguments()
                .getLong(DATA_SELECT_KEY)
        )

        Navigation
            .findNavController(
                this.root)
            .navigate(
                R.id.actionLaunchFragmentShowQR
                , bundleSendData)
    }
}

internal infix fun Context
        .buildResultScanDialog(successResult: Boolean) =
    AlertDialog.Builder(this)
        .setTitle(R.string.infoAlertDialog)
        .setMessage(
            if (successResult)
                R.string.successQRScan
            else
                R.string.faultQRScan)
        .create()

internal infix fun FragmentGeneralAppUserBinding
         .baseUserFragmentUIActions(baseUserFragment: BaseUserFragment)
{
    baseUserFragment.requireActivity().let { activity ->
        if (activity
                .resources
                .configuration
                .orientation ==
            Configuration.ORIENTATION_LANDSCAPE)
        {
            activity.hideBar()
            cameraSurfaceView.scaleX = 2f
            cameraSurfaceView.scaleY = 1f
        }
        else
        {
            activity.setBarOptions(
                R.string.titleFragmentGeneralUser, false
            )
            cameraSurfaceView.scaleX = 1f
            cameraSurfaceView.scaleY = 2f
        }

        activity
            .onBackPressedDispatcher
            .addCallback(activity) {
                activity.finish()
            }

        this.btnScanQR.setOnClickListener {
            it.startAnimation(baseUserFragment.animSelected)

            if (!activity.checkCameraPermissions()
                && activity.checkLocationAvailability())
                return@setOnClickListener

            baseUserFragment.viewModel.liveDataState.set(
                BaseUserModel.ActiveDetectorState(cameraSurfaceView)
            )
        }

        this.btnSettings.setOnClickListener {
            it.startAnimation(baseUserFragment.animSelected)

            Navigation
                .findNavController(this.root)
                .navigate(
                    R.id.actionLaunchSettingsFragment
                )
        }
    }
}

internal infix fun SettingsFragment
        .baseSettingsFragmentUIActions(
    viewModel: InteractionViewModel<SettingsModel>
) {
    requireActivity().setBarOptions(
        R.string.titleSettingsFragment
        , true
    )

    findPreference<SwitchPreference>(getString(R.string.themeAppSettings))
        ?.setOnPreferenceChangeListener {
                _, newValue ->
            viewModel.liveDataState.set(
                SettingsModel.ThemeState(
                    newValue as Boolean
                )
            )
            true
        }

    findPreference<Preference>(getString(R.string.changeLoginSettings))
        ?.setOnPreferenceClickListener {
            viewModel.liveDataState.set(
                SettingsModel.LoginState(
                    this.requireContext()
                )
            )
            false
        }

    findPreference<Preference>(getString(R.string.changePasswordSettings))
        ?.setOnPreferenceClickListener {
            viewModel.liveDataState.set(
                SettingsModel.PasswordState(
                    this.requireContext()
                )
            )
            false
        }

    findPreference<Preference>(getString(R.string.changeOrganizationSettings))
        ?.setOnPreferenceClickListener {
            viewModel.liveDataState.set(
                SettingsModel.OrganizationState(
                    this.requireContext()
                )
            )
            false
        }

    findPreference<Preference>(getString(R.string.removeAccountSettings))
        ?.setOnPreferenceClickListener {
            viewModel.liveDataState.set(
                SettingsModel.RemoveAccountState(
                    this.requireContext()
                )
            )
            false
        }

    findPreference<Preference>(getString(R.string.exitSettings))
        ?.setOnPreferenceClickListener {
            viewModel.liveDataState.set(
                SettingsModel.ExitState(
                    this.requireContext()
                )
            )
            false
        }
}

internal fun FragmentAuthorizationBinding.authorizationFragmentUIActions(
    fragment: AuthorizationFragment
    , viewModel: InteractionViewModel<CommonAuthorizationModel>
) {

    fragment.requireActivity().let {
        it.onBackPressedDispatcher.addCallback(fragment) {
            fragment.requireActivity().finish()
        }

       it.setBarOptions(backButtonActive = false)
    }

    this.btnNextStage.setOnClickListener {
        it.startAnimation(fragment.animSelected)

        viewModel.liveDataState.set(
            CommonAuthorizationModel.LoadingStateAuth(
                this.registerLogin.text.toString()
            )
        )
    }
}

internal fun FragmentConfirmationBinding.confirmationFragmentUIActions(
    fragment: ConfirmationFragment
    , viewModel: InteractionViewModel<CommonAuthorizationModel>
) {
    fragment.requireActivity()
        .setBarOptions(backButtonActive = true)

    this.btnNextStage.setOnClickListener {
        it.startAnimation(fragment.animSelected)

        viewModel.liveDataState.set(
            CommonAuthorizationModel.LoadingStateConfirm(
                fragment.requireArguments().getString(LOGIN_KEY)!!,
                this.inputPassword.text.toString()
            )
        )
    }
}

internal fun FragmentRegistrationBinding.registrationFragmentUIActions(
    fragment: RegistrationFragment
    , viewModel: InteractionViewModel<RegistrationModel>
) {
    fragment.requireActivity()
        .setBarOptions(backButtonActive = true)

    fragment.popupMenuCreator
        .createMenuForSelectUserRole(
            this.btnSelectRole
            , this.registerRoleInfo
            , fragment.animSelected
        )

    this.btnNextStage.setOnClickListener {
        it.startAnimation(fragment.animSelected)

        viewModel.liveDataState.set(
            RegistrationModel.LoadingStageFirstState(
                this.inputPassword.text.toString()
            )
        )
    }
}
