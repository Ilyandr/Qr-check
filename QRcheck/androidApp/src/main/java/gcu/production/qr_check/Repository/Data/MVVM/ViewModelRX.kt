@file:Suppress("PackageName")

package gcu.production.qr_check.Repository.Data.MVVM
import androidx.lifecycle.ViewModel

internal abstract class ViewModelRX <T>
    : ViewModel(), InteractionViewModel<T>
