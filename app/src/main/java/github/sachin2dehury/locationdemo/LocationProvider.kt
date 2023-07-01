package github.sachin2dehury.locationdemo

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationProvider {
    fun getLocationFlow(interval:Long): Flow<Location>
}