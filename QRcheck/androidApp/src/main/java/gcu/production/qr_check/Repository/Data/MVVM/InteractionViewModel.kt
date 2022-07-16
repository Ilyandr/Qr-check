@file:Suppress("PackageName")
package gcu.production.qr_check.Repository.Data.MVVM

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData

internal sealed interface InteractionViewModel <LiveDataType>: InteractionView
{
    val liveDataState: MutableLiveData<LiveDataType>

    override fun registrationOfInteraction(
        additionalData: FragmentActivity?
    ): Any?
}