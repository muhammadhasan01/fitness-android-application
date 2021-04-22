package com.k310.fitness.fragments

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import com.k310.fitness.R
import com.k310.fitness.receiver.AlarmReceiver
import com.k310.fitness.training.TrainingType
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ScheduleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ScheduleFragment : Fragment(), TimePickerDialog.OnTimeSetListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val TAG = "ScheduleFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // TODO: Step 1.7 call create channel
        createChannel(
            getString(R.string.channel_id),
            getString(R.string.channel_name)
        )

        return inflater.inflate(
            R.layout.fragment_schedule, container, false
        )
    }

    private fun createChannel(channelId: String, channelName: String) {
        // TODO: Step 1.6 START create a channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                // TODO: Step 2.4 change importance
                NotificationManager.IMPORTANCE_DEFAULT
            )// TODO: Step 2.6 disable badges for this channel
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.channel_description)

            val notificationManager = requireActivity().getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)

        }
        // TODO: Step 1.6 END create a channel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.schedule_add_btn).setOnClickListener {
            val c: Calendar = Calendar.getInstance()
            val hour = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)

            // Launch Time Picker Dialog
            val timePickerDialog = TimePickerDialog(
                activity, this, hour, minute,
                DateFormat.is24HourFormat(activity)
            )
            timePickerDialog.show()
        }
    }

    override fun onTimeSet(view: TimePicker?, hour: Int, minute: Int) {
        selectTrainingTypeAlertDialog(hour, minute)
    }

    private fun selectTrainingTypeAlertDialog(hour: Int, minute: Int) {
        lateinit var dialog: AlertDialog
        val trainingTypes = arrayOf(TrainingType.CYCLING, TrainingType.RUNNING)

        val builder = AlertDialog.Builder(activity)
        var selectedIdx = 0
        builder.setTitle("Select Activity")

        builder.setSingleChoiceItems(
            trainingTypes.map { it.toString() }.toTypedArray(),
            selectedIdx
        ) { _, which ->
            selectedIdx = which
        }

        builder.setPositiveButton("NEXT") { _, _ ->
            selectRepeatingDayAlertDialog(trainingTypes[selectedIdx], hour, minute)
        }
        dialog = builder.create()
        dialog.show()
    }

    private fun selectRepeatingDayAlertDialog(trainingType: TrainingType, hour: Int, minute: Int) {
        lateinit var dialog: AlertDialog
        val days = arrayOf(
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY,
            DayOfWeek.SUNDAY
        )
        val arrayChecked = booleanArrayOf(false, false, false, false, false, false, false)
        arrayChecked[Calendar.getInstance()
            .get(Calendar.DAY_OF_WEEK) - Calendar.getInstance().firstDayOfWeek - 1] = true

        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Repeat")

        builder.setMultiChoiceItems(days.map {
            it.getDisplayName(
                TextStyle.FULL,
                Locale.getDefault()
            )
        }.toTypedArray(), arrayChecked) { _, which, isChecked ->
            arrayChecked[which] = isChecked
        }

        builder.setPositiveButton("OK") { _, _ ->
            for (i in days.indices) {
                if (arrayChecked[i]) {
                    scheduleAlarm(trainingType, days[i], hour, minute)
                }
            }
        }
        dialog = builder.create()
        dialog.show()
    }

    private inline fun <reified T : Enum<T>> Intent.putExtra(victim: T) =
        putExtra(T::class.java.name, victim.ordinal)

    private fun scheduleAlarm(trainingType: TrainingType, day: DayOfWeek, hour: Int, minute: Int) {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.DAY_OF_WEEK, day.value + Calendar.getInstance().firstDayOfWeek)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, (timeInMillis / 1000).toInt() % 60 + 5) // TODO set jadi 0
            set(Calendar.MILLISECOND, 0)
        }

        val alarmMgr: AlarmManager?
        lateinit var alarmIntent: PendingIntent
        alarmMgr = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmIntent = Intent(context, AlarmReceiver::class.java).let { intent ->
            intent.putExtra(trainingType)
            PendingIntent.getBroadcast(context, 0, intent, 0)
        }

        alarmMgr.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY * 7,
            alarmIntent
        )
        Log.i(TAG, "Scheduled $trainingType on ${Date(calendar.timeInMillis)}")
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ScheduleFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ScheduleFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}