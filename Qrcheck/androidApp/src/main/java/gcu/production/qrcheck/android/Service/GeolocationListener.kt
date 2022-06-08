package gcu.production.qrcheck.android.Service

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
import gcu.production.qrcheck.android.R
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@DelicateCoroutinesApi
internal class GeolocationListener(
    private val context: Context
    , private val locationAction: (
        location: Location) -> Unit)
{
    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)


    @SuppressLint("MissingPermission")
    fun launch(actionForFault: (() -> Unit)? = null)
    {
        if (!(this.context.getSystemService(
                Context.LOCATION_SERVICE) as LocationManager)
                .isProviderEnabled(
                    LocationManager.GPS_PROVIDER))
        {
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

        fusedLocationClient.lastLocation.addOnSuccessListener {
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