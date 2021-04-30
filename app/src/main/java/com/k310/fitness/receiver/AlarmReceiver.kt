package com.k310.fitness.receiver

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import androidx.core.content.ContextCompat
import com.k310.fitness.util.sendNotification
import com.k310.fitness.util.training.TrainingType
import timber.log.Timber

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

        val isStartTraining = intent.extras?.getBoolean("is_start_training")
        val runInBackground = intent.extras?.getBoolean("run_in_background")
        if (isStartTraining == true) {
            val trainingType = intent.getEnumExtra<TrainingType>()
            val target = intent.extras?.getFloat("target")
            val targetType = if (trainingType == TrainingType.CYCLING) "kilometers" else "steps"
            Timber.i("Alarm $trainingType")
            val notificationManager = ContextCompat.getSystemService(
                context,
                NotificationManager::class.java
            ) as NotificationManager

            notificationManager.sendNotification(
                "$trainingType, target: $target $targetType",
                context
            )
            if(runInBackground == true){
                context.startService(intent)
            }
        } else {
            val notificationManager = ContextCompat.getSystemService(
                context,
                NotificationManager::class.java
            ) as NotificationManager

            notificationManager.sendNotification(
                "Training done! Take a rest.",
                context
            )
        }

        wl.release()
    }
}
