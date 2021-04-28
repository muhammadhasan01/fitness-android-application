package com.k310.fitness.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.k310.fitness.R
import com.k310.fitness.ui.activities.MainActivity
import com.k310.fitness.util.Constants.ACTION_PAUSE_SERVICE
import com.k310.fitness.util.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.k310.fitness.util.Constants.ACTION_START_OR_RESUME_SERVICE
import com.k310.fitness.util.Constants.ACTION_STOP_SERVICE
import com.k310.fitness.util.Constants.NOTIFICATION_CHANNEL_ID
import com.k310.fitness.util.Constants.NOTIFICATION_CHANNEL_NAME
import com.k310.fitness.util.Constants.NOTIFICATION_ID
import timber.log.Timber

class TrackingService : LifecycleService() {

    var isFirst = true

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    if (isFirst) {
                        startForegroundService()
                        isFirst = false
                    } else {
                        Timber.d("Resuming service")
                    }
                }
                ACTION_PAUSE_SERVICE -> {
                    Timber.d("Service paused")
                }
                ACTION_STOP_SERVICE -> {
                    Timber.d("Service stopped")
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundService() {
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