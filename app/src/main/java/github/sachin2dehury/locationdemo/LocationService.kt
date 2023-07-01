package github.sachin2dehury.locationdemo

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@AndroidEntryPoint
class LocationService : Service() {

    @Inject
    lateinit var locationProvider: LocationProvider

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            START -> start()
            STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun stop() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun start() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(this, NOTIFICATION)
            .setOngoing(true)
            .setContentTitle("Location")
            .setContentText("Location is null")
            .setSmallIcon(R.drawable.ic_launcher_background)


        locationProvider.getLocationFlow(1000L)
            .catch { it.printStackTrace() }
            .onEach {
                val lat = it.latitude
                val long = it.longitude
                notification.setContentText("Location is $lat $long")
                notificationManager.notify(1, notification.build())
            }.launchIn(scope)

        startForeground(1, notification.build())
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    companion object {
        const val START = "start"
        const val STOP = "stop"
        const val NOTIFICATION = "location"
    }
}