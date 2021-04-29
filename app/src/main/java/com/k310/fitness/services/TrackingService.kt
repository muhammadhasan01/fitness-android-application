package com.k310.fitness.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.k310.fitness.R
import com.k310.fitness.ui.activities.MainActivity
import com.k310.fitness.util.Constants.ACTION_PAUSE_SERVICE
import com.k310.fitness.util.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.k310.fitness.util.Constants.ACTION_START_OR_RESUME_SERVICE
import com.k310.fitness.util.Constants.ACTION_STOP_SERVICE
import com.k310.fitness.util.Constants.FASTEST_LOCATION_INTERVAL
import com.k310.fitness.util.Constants.LOCATION_UPDATE_INTERVAL
import com.k310.fitness.util.Constants.NOTIFICATION_CHANNEL_ID
import com.k310.fitness.util.Constants.NOTIFICATION_CHANNEL_NAME
import com.k310.fitness.util.Constants.NOTIFICATION_ID
import com.k310.fitness.util.Constants.TIMER_UPDATE_INTERVAL
import com.k310.fitness.util.TrackingUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>

class TrackingService : LifecycleService() {

    var isFirst = true
    var serviceKilled = false

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val runtimeInS = MutableLiveData<Long>()

    companion object {
        val runtimeInMs = MutableLiveData<Long>()
        val isTracking = MutableLiveData<Boolean>()
        val cordPoints = MutableLiveData<Polylines>()
    }

    private fun postInitValues() {
        isTracking.postValue(false)
        cordPoints.postValue(mutableListOf())
        runtimeInS.postValue(0L)
        runtimeInMs.postValue(0L)
    }

    override fun onCreate() {
        super.onCreate()
        postInitValues()
        fusedLocationProviderClient = FusedLocationProviderClient(this)
        isTracking.observe(this, Observer {
            updateLocation(it)
        })
    }

    private fun killService() {
        serviceKilled = true
        isFirst = true
        pauseService()
        postInitValues()
        stopForeground(true)
        stopSelf()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    if (isFirst) {
                        startForegroundService()
                        isFirst = false
                    } else {
                        Timber.d("Resuming service")
                        startTimer()
                    }
                }
                ACTION_PAUSE_SERVICE -> {
                    Timber.d("Service paused")
                    pauseService()
                }
                ACTION_STOP_SERVICE -> {
                    Timber.d("Service stopped")
                    killService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private var isTimerEnabled = false
    private var lapTime = 0L
    private var runtime = 0L
    private var timeStarted = 0L
    private var lastTimestamp = 0L

    private fun startTimer() {
        addEmptyPolyline()
        isTracking.postValue(true)
        timeStarted = System.currentTimeMillis()
        isTimerEnabled = true
        CoroutineScope(Dispatchers.Main).launch {
            while(isTracking.value!!) {
                lapTime = System.currentTimeMillis() - timeStarted
                runtimeInMs.postValue((runtime + lapTime))
                if(runtimeInMs.value!! >= lastTimestamp + 1000L) {
                    runtimeInS.postValue(runtimeInS.value!! + 1)
                    lastTimestamp += 1000L
                }
                delay(TIMER_UPDATE_INTERVAL)
            }
            runtime += lapTime
        }
    }

    private fun pauseService() {
        isTracking.postValue(false)
        isTimerEnabled = false
    }

    @SuppressLint("MissingPermission")
    private fun updateLocation(isTracking: Boolean) {
        if(isTracking) {
            if (TrackingUtil.hasLocPermission(this)) {
                val request = LocationRequest.create().apply {
                    interval = LOCATION_UPDATE_INTERVAL
                    fastestInterval = FASTEST_LOCATION_INTERVAL
                    priority = PRIORITY_HIGH_ACCURACY
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            if (isTracking.value!!) {
                result.locations.let { locations ->
                    for(location in locations) {
                        addCordPoints(location)
                        Timber.d("NEW LOCATION: ${location.latitude}, ${location.longitude})")
                    }
                }
            }
        }
    }

    private fun addCordPoints(location: Location?) {
        location?.let {
            val pos = LatLng(location.latitude, location.longitude)
            cordPoints.value?.apply {
                last().add(pos)
                cordPoints.postValue(this)
            }
        }
    }

    private fun addEmptyPolyline() = cordPoints.value?.apply {
        add(mutableListOf())
        cordPoints.postValue(this)
    } ?: cordPoints.postValue(mutableListOf(mutableListOf()))

    private fun startForegroundService() {
        startTimer()
        isTracking.postValue(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.icon_run)
            .setContentTitle("Fitness")
            .setContentText("00:00:00")
            .setContentIntent(getPendingIntent())

        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }



    private fun getPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java).also {
            it.action = ACTION_SHOW_TRACKING_FRAGMENT
        },
        FLAG_UPDATE_CURRENT
    )

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel (notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }
}