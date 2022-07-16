@file:Suppress("PackageName")
package gcu.production.qr_check.Repository.Data.MVVM

import androidx.fragment.app.FragmentActivity

internal interface InteractionView
{
    fun registrationOfInteraction(
        additionalData: FragmentActivity? = null
    ): Any?
}