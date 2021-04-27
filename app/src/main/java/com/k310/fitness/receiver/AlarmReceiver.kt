package com.k310.fitness.receiver

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.k310.fitness.R
import com.k310.fitness.training.TrainingType
import com.k310.fitness.util.sendNotification


class AlarmReceiver : BroadcastReceiver() {
    private val TAG = "AlarmReceiver"

    private inline fun <reified T : Enum<T>> Intent.getEnumExtra(): T? =
        getIntExtra(T::class.java.name, -1)
            .takeUnless { it == -1 }
            ?.let { T::class.java.enumConstants[it] }


    @SuppressLint("InvalidWakeLockTag")
    override fun onReceive(context: Context, intent: Intent) {
        val pm: PowerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wl: PowerManager.WakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG)

        wl.acquire(10 * 60 * 1000L /*10 minutes*/)

        Log.i(TAG, "Alarm ${intent.getEnumExtra<TrainingType>()}")
        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager

        notificationManager.sendNotification(
            context.getText(R.string.training_ready).toString(),
            context
        )

        wl.release()
    }
}
