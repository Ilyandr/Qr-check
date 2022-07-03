@file:Suppress("PackageName")
package gcu.production.qr_check.GeneralAppUI

import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity

internal object ActionBarSettings
{
    fun FragmentActivity.setBarOptions(
        @StringRes titleId: Int? = null
        , backButtonActive: Boolean)
    {
        (this as AppCompatActivity).supportActionBar?.title =
            titleId?.let { this.getString(it) } ?: " "

        this.supportActionBar
            ?.setDisplayHomeAsUpEnabled(backButtonActive)
    }
}