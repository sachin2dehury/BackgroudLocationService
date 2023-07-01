package github.sachin2dehury.locationdemo

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class LocationProviderImpl(
    private val context: Context,
    private val client: FusedLocationProviderClient
) : LocationProvider {

    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    @SuppressLint("MissingPermission")
    override fun getLocationFlow(interval: Long): Flow<Location> {
        return callbackFlow {
            if (hasLocationAccess()) {
                val isGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                val isNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

                if (!isGps && !isNetwork) {
                    throw LocationException("Gps not enabled")
                } else {
                    val request = LocationRequest.Builder(interval)
                        .build()
                    val callback = object : LocationCallback() {
                        override fun onLocationResult(result: LocationResult) {
                            super.onLocationResult(result)
                            result.locations.lastOrNull()?.let {
                                launch {
                                    send(it)
                                }
                            }
                        }
                    }

                    client.requestLocationUpdates(request, callback, Looper.getMainLooper())

                    awaitClose {
                        client.removeLocationUpdates(callback)
                    }
                }

            } else {
                throw LocationException("Location Access Not Provided")
            }
        }
    }

    private fun hasLocationAccess(): Boolean {
        val coarseLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val fineLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return coarseLocation && fineLocation
    }
}