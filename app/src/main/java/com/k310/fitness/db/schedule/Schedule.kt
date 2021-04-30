package com.k310.fitness.db.schedule

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.k310.fitness.util.training.RepeatType
import com.k310.fitness.util.training.TrainingType
import java.util.*

@Entity(tableName = "schedules")
data class Schedule(
    val trainingType: TrainingType,
    val repeatType: RepeatType,
    var nextInvocation: Calendar,
    val duration: Long,
    val target: Float,
    val runInBackground: Boolean,
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var scheduleId: Int = 0
    override fun toString() = "$repeatType $trainingType on ${Date(nextInvocation.timeInMillis)} for ${duration / 60 / 1000} minutes, target: $target, run in background: $runInBackground"
}