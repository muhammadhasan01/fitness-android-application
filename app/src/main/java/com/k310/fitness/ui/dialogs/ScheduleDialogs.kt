package com.k310.fitness.ui.dialogs

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.DialogInterface
import android.text.format.DateFormat
import androidx.fragment.app.FragmentActivity
import com.k310.fitness.ui.viewmodels.MainViewModel
import com.k310.fitness.util.Alarm
import com.k310.fitness.util.training.RepeatType
import com.k310.fitness.util.training.TrainingType
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*


class ScheduleDialogs(
    private val activity: FragmentActivity?,
    private val viewModel: MainViewModel,
) {
    private val TAG = "ScheduleDialogs"

    fun show() {
        val alarm = Alarm.Builder()

        selectTimeDialog(
            onTimeSetListener = { _, hourOfDay, minute ->
                alarm.hourOfDay(hourOfDay)
                alarm.minute(minute)

                val trainingTypes = arrayOf(TrainingType.CYCLING, TrainingType.RUNNING)

                selectTrainingTypeAlertDialog(
                    trainingTypes,
                    onSelect = { _, which ->
                        alarm.trainingType(trainingTypes[which])
                    },
                    next = { _, _ ->
                        val durations = arrayOf(5, 10, 15, 30, 60, 90, 120)

                        selectDurationAlertDialog(
                            durations,
                            onSelect = { _, which ->
                                alarm.duration(durations[which].toLong() * 60 * 1000)
                            },
                            next = { _, _ ->
                                selectRepeatTypeAlertDialog(
                                    nextRepeating = { _, _ ->
                                        alarm.repeatType(RepeatType.WEEKLY)
                                        val days = arrayOf(
                                            DayOfWeek.MONDAY,
                                            DayOfWeek.TUESDAY,
                                            DayOfWeek.WEDNESDAY,
                                            DayOfWeek.THURSDAY,
                                            DayOfWeek.FRIDAY,
                                            DayOfWeek.SATURDAY,
                                            DayOfWeek.SUNDAY
                                        )

                                        val arrayChecked =
                                            booleanArrayOf(
                                                false,
                                                false,
                                                false,
                                                false,
                                                false,
                                                false,
                                                false
                                            )
                                        arrayChecked[Calendar.getInstance()
                                            .get(Calendar.DAY_OF_WEEK) - Calendar.getInstance().firstDayOfWeek - 1] =
                                            true

                                        selectRepeatingDayAlertDialog(
                                            days,
                                            arrayChecked,
                                            onSelect = { _, which, isChecked ->
                                                arrayChecked[which] = isChecked
                                            },
                                            next = { _, _ ->
                                                for (i in days.indices) {
                                                    if (arrayChecked[i]) {
                                                        alarm.dayOfWeek(days[i].value + Calendar.getInstance().firstDayOfWeek)
                                                        activity?.let {
                                                            alarm.build().set(it, viewModel)
                                                        }
                                                    }
                                                }
                                            })
                                    },

                                    nextNonRepeating = { _, _ ->
                                        alarm.repeatType(RepeatType.ONE_TIME)
                                        selectOneTimeDayAlertDialog(
                                            onDateSetListener = { _, year, monthOfYear, dayOfMonth ->
                                                alarm.year(year)
                                                alarm.monthOfYear(monthOfYear)
                                                alarm.dayOfMonth(dayOfMonth)
                                                activity?.let {
                                                    alarm.build().set(it, viewModel)
                                                }
                                            }
                                        )
                                    }
                                )
                            }
                        )
                    }
                )
            }
        )
    }

    private fun selectTimeDialog(onTimeSetListener: OnTimeSetListener) {
        val c: Calendar = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            activity, onTimeSetListener, hour, minute,
            DateFormat.is24HourFormat(activity)
        )
        timePickerDialog.show()
    }

    private fun selectTrainingTypeAlertDialog(
        trainingTypes: Array<TrainingType>,
        onSelect: DialogInterface.OnClickListener,
        next: DialogInterface.OnClickListener
    ) {
        lateinit var dialog: AlertDialog

        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Select Activity")
        builder.setSingleChoiceItems(
            trainingTypes.map { it.toString() }.toTypedArray(), 0, onSelect
        )
        builder.setPositiveButton("NEXT", next)
        dialog = builder.create()
        dialog.show()
    }

    private fun selectDurationAlertDialog(
        durations: Array<Int>,
        onSelect: DialogInterface.OnClickListener,
        next: DialogInterface.OnClickListener
    ) {
        lateinit var dialog: AlertDialog
        val durationsString = durations.map { "$it minutes" }

        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Select Duration")

        builder.setSingleChoiceItems(durationsString.toTypedArray(), 0, onSelect)
        builder.setPositiveButton("NEXT", next)
        dialog = builder.create()
        dialog.show()
    }

    private fun selectRepeatTypeAlertDialog(
        nextRepeating: DialogInterface.OnClickListener,
        nextNonRepeating: DialogInterface.OnClickListener
    ) {
        lateinit var dialog: AlertDialog

        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Repeating?")
        builder.setPositiveButton("YES", nextRepeating)
        builder.setNegativeButton("NO", nextNonRepeating)

        dialog = builder.create()
        dialog.show()
    }

    private fun selectRepeatingDayAlertDialog(
        days: Array<DayOfWeek>,
        arrayChecked: BooleanArray,
        onSelect: DialogInterface.OnMultiChoiceClickListener,
        next: DialogInterface.OnClickListener
    ) {
        lateinit var dialog: AlertDialog

        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Repeat")

        builder.setMultiChoiceItems(days.map {
            it.getDisplayName(
                TextStyle.FULL,
                Locale.getDefault()
            )
        }.toTypedArray(), arrayChecked, onSelect)

        builder.setPositiveButton("OK", next)
        dialog = builder.create()
        dialog.show()
    }

    private fun selectOneTimeDayAlertDialog(onDateSetListener: DatePickerDialog.OnDateSetListener) {
        val c = Calendar.getInstance()
        val mYear = c[Calendar.YEAR]
        val mMonth = c[Calendar.MONTH]
        val mDay = c[Calendar.DAY_OF_MONTH]

        val datePickerDialog = activity?.let {
            DatePickerDialog(
                it,
                onDateSetListener,
                mYear,
                mMonth,
                mDay
            )
        }
        datePickerDialog?.show()
    }
}