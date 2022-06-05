package gcu.production.qrcheck.android.GeneralAppUI

import android.annotation.SuppressLint
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import gcu.production.qrcheck.android.R

internal class CustomLoadingDialog(
    private val activityCall: FragmentActivity)
{
    private var loadingDialog: AlertDialog? = null

    @SuppressLint("InflateParams")
    internal fun startLoadingDialog() =
        this.loadingDialog?.show() ?: run {
            val dialogBuilder =
                AlertDialog.Builder(this.activityCall).setView(
                    this.activityCall
                        .layoutInflater
                        .inflate(
                            R.layout.custom_loading_dialog, null
                        )
                ).setCancelable(false)

            this.loadingDialog = dialogBuilder.create()
            this.loadingDialog!!.show()
        }

    internal fun stopLoadingDialog() =
        this.loadingDialog?.dismiss()
}