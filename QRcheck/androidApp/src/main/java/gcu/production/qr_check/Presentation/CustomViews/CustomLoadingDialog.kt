@file:Suppress("PackageName")
package gcu.production.qr_check.Presentation.CustomViews

import android.annotation.SuppressLint
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import gcu.production.qr_check.android.R

internal class CustomLoadingDialog @AssistedInject constructor(
    @Assisted("activityCall")
    private val activityCall: FragmentActivity
)
{
    private var loadingDialog: AlertDialog? = null

    @SuppressLint("InflateParams")
    fun startLoadingDialog() =
        this.loadingDialog?.show() ?: run {
            val dialogBuilder =
                AlertDialog.Builder(activityCall)
                    .setView(
                        activityCall.layoutInflater.inflate(
                            R.layout.custom_loading_dialog
                            , null
                        )
                ).setCancelable(false)

            this.loadingDialog = dialogBuilder.create()
            this.loadingDialog!!.show()
        }

    internal fun stopLoadingDialog() =
        this.loadingDialog?.dismiss()
}