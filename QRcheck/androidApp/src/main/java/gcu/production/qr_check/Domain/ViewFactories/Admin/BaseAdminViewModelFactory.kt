@file:Suppress("PackageName", "UNCHECKED_CAST")
package gcu.production.qr_check.Domain.ViewFactories.Admin

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import gcu.production.qr_check.Domain.ViewModels.Admin.BaseAdminViewModel

internal class BaseAdminViewModelFactory @AssistedInject constructor (
    @Assisted(value = "fragmentCall")
    private val fragmentCall: FragmentActivity
) : ViewModelProvider.NewInstanceFactory()
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T
    {
        if (modelClass.isAssignableFrom(BaseAdminViewModel::class.java))
           return BaseAdminViewModel(this.fragmentCall) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}