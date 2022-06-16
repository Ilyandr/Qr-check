package gcu.production.qr_check.Service

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.OnTokenCanceledListener
import gcu.production.qr_check.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

actual class GeolocationListener(
    private val context: Context
    , private val locationAction: (location: Location) -> Unit
    , private val actionFault: Runnable? = null)
{
    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    actual fun launch(actionForFault: (() -> Unit)?)
    {
        if (!(this.context.getSystemService(
                Context.LOCATION_SERVICE) as LocationManager)
                .isProviderEnabled(
                    LocationManager.GPS_PROVIDER))
        {
            this.actionFault?.run()

            AlertDialog.Builder(this.context)
                .setTitle(R.string.infoAlertDialog)
                .setMessage(R.string.alertDialogOnGPS)
                .setPositiveButton(R.string.btnTextRegisterChangeStage)
                { _, _ ->
                    this.context.startActivity(
                        Intent(
                            Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    )
                }.setNegativeButton(R.string.infoCloseAlert)
                { it, _ ->
                    actionForFault?.invoke()
                    it.cancel()
                }.create().show()
            return
        }

        this.fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY
            , object: CancellationToken()
            {
                override fun onCanceledRequested(p0: OnTokenCanceledListener): CancellationToken
                {
                    GlobalScope.launch(Dispatchers.Main) {
                        actionFault?.run()
                    }
                    return this
                }

                override fun isCancellationRequested(): Boolean = true
            })
            .addOnSuccessListener {
                GlobalScope.launch(Dispatchers.Main) {
                    this@GeolocationListener.locationAction(it)
                }
            }
    }

    companion object
    {
        fun FragmentActivity.checkPermissions() =
            if (ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(
                    this
                    , arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION
                        , Manifest.permission.CAMERA)
                    , 535
                )
                false
            } else true
    }
}