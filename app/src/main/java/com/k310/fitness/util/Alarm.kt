package com.k310.fitness.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.MediatorLiveData
import com.k310.fitness.db.schedule.Schedule
import com.k310.fitness.receiver.AlarmReceiver
import com.k310.fitness.ui.viewmodels.MainViewModel
import com.k310.fitness.util.training.RepeatType
import com.k310.fitness.util.training.TrainingType
import timber.log.Timber
import java.util.*

class Alarm(
    var dayOfMonth: Int = 1,
    var dayOfWeek: Int = 1,
    var year: Int = 2021,
    var monthOfYear: Int = 1,
    var duration: Long = 5 * 60 * 1000,
    var trainingType: TrainingType = TrainingType.CYCLING,
    var repeatType: RepeatType = RepeatType.WEEKLY,
    var hourOfDay: Int = 0,
    var minute: Int = 0,
    var target: Float,
) {

    private val TAG = "Alarm"
    val schedules = MediatorLiveData<List<Schedule>>()

    data class Builder(
        var dayOfMonth: Int = 1,
        var dayOfWeek: Int = 1,
        var year: Int = 2021,
        var monthOfYear: Int = 1,
        var duration: Long = 5 * 60 * 1000,
        var trainingType: TrainingType = TrainingType.CYCLING,
        var repeatType: RepeatType = RepeatType.WEEKLY,
        var hourOfDay: Int = 0,
        var minute: Int = 0,
        var target: Float = 0f
    ) {

        fun repeatType(repeatType: RepeatType) = apply { this.repeatType = repeatType }
        fun dayOfMonth(dayOfMonth: Int) = apply { this.dayOfMonth = dayOfMonth }
        fun dayOfWeek(dayOfWeek: Int) = apply { this.dayOfWeek = dayOfWeek }
        fun year(year: Int) = apply { this.year = year }
        fun monthOfYear(monthOfYear: Int) = apply { this.monthOfYear = monthOfYear }
        fun duration(duration: Long) = apply { this.duration = duration }
        fun trainingType(trainingType: TrainingType) = apply { this.trainingType = trainingType }
        fun hourOfDay(hourOfDay: Int) = apply { this.hourOfDay = hourOfDay }
        fun minute(minute: Int) = apply { this.minute = minute }
        fun target(target: Float) = apply { this.target = target }

        fun build() = Alarm(
            dayOfMonth, dayOfWeek,
            year, monthOfYear,
            duration,
            trainingType, repeatType,
            hourOfDay, minute,
            target
        )
    }

    private inline fun <reified T : Enum<T>> Intent.putExtra(victim: T) =
        putExtra(T::class.java.name, victim.ordinal)

    fun set(context: Context, viewModel: MainViewModel) {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
            set(
                Calendar.SECOND,
                (timeInMillis / 1000).toInt() % 60 + 10
            ) // TODO set jadi 0
            set(Calendar.MILLISECOND, 0)
        }
        if (repeatType.equals(RepeatType.ONE_TIME)) {
            calendar.apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, monthOfYear)
                set(Calendar.DAY_OF_MONTH, dayOfMonth)
            }
        } else if (repeatType.equals(RepeatType.WEEKLY)) {
            calendar.apply {
                set(Calendar.DAY_OF_WEEK, dayOfWeek)
            }
        }

        val alarmMgr: AlarmManager?
        alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent: PendingIntent =
            Intent(context, AlarmReceiver::class.java).let { intent ->
                intent.putExtra(trainingType)
                PendingIntent.getBroadcast(context, 0, intent, 0)
            }

        alarmMgr.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY * 7,
            alarmIntent
        )

        viewModel.insertSchedule(Schedule(trainingType, repeatType, calendar, duration, target)).toString()
            .also {
                Timber.i(
                    "Scheduled $repeatType $trainingType on ${Date(calendar.timeInMillis)} until ${
                        Date(calendar.timeInMillis + duration)
                    }"
                )
            }
    }
}