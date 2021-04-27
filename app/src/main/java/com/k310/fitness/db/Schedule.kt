package com.k310.fitness.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.k310.fitness.training.RepeatType
import com.k310.fitness.training.TrainingType
import java.util.*

@Entity(tableName = "schedules")
data class Schedule(
    val trainingType: TrainingType,
    val repeatType: RepeatType,
    var nextInvocation: Calendar,
    val duration: Long,
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var scheduleId: Int = 0
    override fun toString() = "$repeatType $trainingType on ${Date(nextInvocation.timeInMillis)} for ${duration/1000/60} minutes"
}