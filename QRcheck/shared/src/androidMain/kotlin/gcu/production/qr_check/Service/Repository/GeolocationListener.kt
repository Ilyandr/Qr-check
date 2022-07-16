@file:Suppress("PackageName", "LABEL_NAME_CLASH")
package gcu.production.qr_check.Service.Repository

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.location.*
import gcu.production.qr_check.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

actual class GeolocationListener actual constructor()
{
    @Suppress("DEPRECATION")
    val locationRequest: LocationRequest by lazy {
        LocationRequest().apply {
            maxWaitTime = TimeUnit.MINUTES.toMillis(2)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    var fusedLocationClient: FusedLocationProviderClient? = null

    fun init(applicationData: FragmentActivity): GeolocationListener
    {
        this.fusedLocationClient =
            LocationServices
                .getFusedLocationProviderClient(
                    applicationData
                )
        return this
    }

    @SuppressLint("MissingPermission")
    actual inline fun launch(
        additionalDataPoint: Pair<Int, Long?>?
        , additionalDataScan: String?
        , crossinline actionForFault: (() -> Unit)
        , noinline actionForSuccessPoint: actionSuccessCreatePointGeo?
        , noinline actionForSuccessScan: actionSuccessScanQRGeo?)
    {
        if (this.fusedLocationClient == null)
            return

        this.fusedLocationClient?.requestLocationUpdates(
            this.locationRequest
            , object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    locationResult.lastLocation?.let {
                        Log.e("info", it.toString())

                        GlobalScope.launch(Dispatchers.IO)  {
                            actionForSuccessPoint?.invoke(
                                it.latitude, it.longitude, additionalDataPoint
                                    ?: run {
                                        actionForFault.invoke()
                                        return@launch
                                    }
                            )

                            actionForSuccessScan?.invoke(
                                it.latitude, it.longitude, additionalDataScan
                                    ?: run {
                                        actionForFault.invoke()
                                        return@launch
                                    }
                            )
                        }
                        fusedLocationClient?.removeLocationUpdates(this)
                    }
                }
            }, Looper.getMainLooper()
        )
    }

    companion object
    {
        fun FragmentActivity.checkLocationAvailability(): Boolean
        {
            if (ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(
                    this
                    , arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION)
                    , 535
                )
                return false
            }

            if (!(this.getSystemService(
                    Context.LOCATION_SERVICE) as LocationManager)
                    .isProviderEnabled(
                        LocationManager.GPS_PROVIDER))
            {
                AlertDialog.Builder(this)
                    .setTitle(R.string.infoAlertDialog)
                    .setMessage(R.string.alertDialogOnGPS)
                    .setPositiveButton(R.string.btnTextRegisterChangeStage)
                    { _, _ ->
                        this.startActivity(
                            Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        )
                    }.setNegativeButton(R.string.infoCloseAlert)
                    { it, _ -> it.cancel() }
                    .create()
                    .show()
                return false
            }
            else return true
        }
    }
}