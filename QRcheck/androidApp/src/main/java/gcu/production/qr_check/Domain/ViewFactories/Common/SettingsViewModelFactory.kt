@file:Suppress("PackageName", "UNCHECKED_CAST")
package gcu.production.qr_check.Domain.ViewFactories.Common

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import gcu.production.qr_check.Domain.ViewModels.Common.SettingsViewModel

internal class SettingsViewModelFactory @AssistedInject constructor (
    @Assisted(value = "fragmentCall")
    private val fragmentCall: FragmentActivity
) : ViewModelProvider.NewInstanceFactory()
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T
    {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java))
            return SettingsViewModel(this.fragmentCall) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}