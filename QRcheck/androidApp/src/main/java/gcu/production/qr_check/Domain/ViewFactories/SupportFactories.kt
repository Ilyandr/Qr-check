@file:Suppress("PackageName")
package gcu.production.qr_check.Domain.ViewFactories

import androidx.fragment.app.FragmentActivity
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import gcu.production.qr_check.Domain.ViewFactories.Admin.BaseAdminViewModelFactory
import gcu.production.qr_check.Domain.ViewFactories.Admin.RecordsViewModelFactory
import gcu.production.qr_check.Domain.ViewFactories.Admin.ShowQRVMFactory
import gcu.production.qr_check.Domain.ViewFactories.Common.SettingsViewModelFactory
import gcu.production.qr_check.Domain.ViewFactories.User.BaseUserViewModelFactory
import gcu.production.qr_check.Presentation.CustomViews.CustomLoadingDialog
import gcu.production.qr_check.Repository.Features.Detectors.BarcodeDetectorService
import gcu.production.qr_check.Repository.Features.Detectors.inputBarcode
import javax.inject.Singleton

@AssistedFactory
internal interface SupportBaseFragmentFactory
{
    fun create(
        @Assisted(value = "fragmentCall")
        fragmentCall: FragmentActivity
    ): BaseAdminViewModelFactory
}

@AssistedFactory
@Singleton
internal interface CustomLoadingDialogFactory
{
    fun create(
        @Assisted(value = "activityCall")
        fragmentCall: FragmentActivity
    ): CustomLoadingDialog
}

@AssistedFactory
@Singleton
internal interface SupportShowQRVMFactory
{
    fun create(
        @Assisted(value = "fragmentCall")
        fragmentCall: FragmentActivity
    ): ShowQRVMFactory
}

@AssistedFactory
internal interface SupportRecordsFragmentFactory
{
    fun create(
        @Assisted(value = "fragmentCall")
        fragmentCall: FragmentActivity
    ): RecordsViewModelFactory
}

@AssistedFactory
internal interface SupportBaseUserViewModelFactory
{
    infix fun create(
        @Assisted(value = "fragmentCall")
        fragmentCall: FragmentActivity
    ): BaseUserViewModelFactory
}

@AssistedFactory
internal interface SupportSettingsViewModelFactory
{
    infix fun create(
        @Assisted(value = "fragmentCall")
        fragmentCall: FragmentActivity
    ): SettingsViewModelFactory
}

@AssistedFactory
internal interface BarcodeDetectorServiceFactory
{
    infix fun create(
        @Assisted(value = "actionDetected")
        actionDetected: inputBarcode
    ): BarcodeDetectorService
}