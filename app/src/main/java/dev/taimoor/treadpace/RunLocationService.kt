package dev.taimoor.treadpace

import android.app.IntentService
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.os.Binder
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task

class RunLocationService: Service() {

    lateinit var pendingIntent: PendingIntent
    lateinit var notification: Notification
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val locationRequest = intent?.getParcelableExtra<LocationRequest>("locationRequest")

        locationRequest?.let {
            this.startRun(it)
        }

        startForeground(60, notification)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        Log.i(Util.myTag, "From Ending")
        stopSelf()

    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }


    fun startRun(locationRequest: LocationRequest){
        locationRequest?.let {
            this.startLocationUpdates(it)
        }
    }

    private fun startLocationUpdates(locationRequest: LocationRequest) {
        fusedLocationClient.requestLocationUpdates(locationRequest,
            locationCallback,
            Looper.getMainLooper())
    }

    override fun onCreate(){

        val intent = Intent(this, MainActivity::class.java).apply {
            this.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        }

        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val builder = NotificationCompat.Builder(this as Context, "60")
        builder.setContentTitle("Notification Title")
            .setContentText("Notification text")
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notification = builder.build()

        this.locationCallback = object : LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return

                for(location in locationResult.locations){
                    Log.i("LocationService", "LATITUDE: ${location.latitude}\t LONGITUDE:${location.longitude}")
                    Toast.makeText(applicationContext, "LATITUDE: ${location.latitude}\t LONGITUDE:${location.longitude}", Toast.LENGTH_LONG).show()
                }
            }
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)
    }
}
