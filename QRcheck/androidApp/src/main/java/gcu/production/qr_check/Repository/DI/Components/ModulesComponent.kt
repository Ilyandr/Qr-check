@file:Suppress("PackageName")
@file:OptIn(DelicateCoroutinesApi::class)

package gcu.production.qr_check.Repository.DI.Components

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import gcu.production.qr_check.Domain.ViewModels.User.BaseUserViewModel
import gcu.production.qr_check.Presentation.Main.Admin.BaseAdminFragment
import gcu.production.qr_check.Presentation.Main.Admin.FragmentShowQR
import gcu.production.qr_check.Presentation.Main.Admin.RecordsFragment
import gcu.production.qr_check.Presentation.Main.Common.Authorization.AuthorizationFragment
import gcu.production.qr_check.Presentation.Main.Common.Authorization.ConfirmationFragment
import gcu.production.qr_check.Presentation.Main.Common.Authorization.LaunchControllerFragment
import gcu.production.qr_check.Presentation.Main.Common.Authorization.RegistrationFragment
import gcu.production.qr_check.Presentation.Main.Common.BasicActivity
import gcu.production.qr_check.Presentation.Main.Common.SettingsFragment
import gcu.production.qr_check.Presentation.Main.User.BaseUserFragment
import gcu.production.qr_check.Repository.DI.Modules.UIModule
import kotlinx.coroutines.DelicateCoroutinesApi
import javax.inject.Singleton

@Component(modules = [UIModule::class])
@Singleton
internal interface ModulesComponent
{
    @Component.Builder
    interface Builder
    {
        @BindsInstance
        fun contextApplication(context: Context): Builder

        fun build(): ModulesComponent
    }

    fun inject(baseAdminFragment: BaseAdminFragment)
    fun inject(showQR: FragmentShowQR)
    fun inject(recordsFragment: RecordsFragment)
    fun inject(baseUserFragment: BaseUserFragment)
    fun inject(baseUserViewModel: BaseUserViewModel)
    fun inject(settingsFragment: SettingsFragment)
    fun inject(authorizationFragment: AuthorizationFragment)
    fun inject(confirmationFragment: ConfirmationFragment)
    fun inject(registrationFragment: RegistrationFragment)
    fun inject(launchControllerFragment: LaunchControllerFragment)
    fun inject(basicActivity: BasicActivity)
}