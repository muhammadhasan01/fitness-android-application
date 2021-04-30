package com.k310.fitness.db.training

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.k310.fitness.util.training.TrainingType
import java.util.*

@Entity(tableName = "training_table")
data class Training(
    var img: Bitmap? = null,
    var trainingType: TrainingType,
    var startTime: Calendar,
    var stopTime: Calendar,
    var detail: Float,
    var duration: Long,
    var avgInKMH: Float,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
    override fun toString(): String {
        val unit = if (trainingType == TrainingType.CYCLING) "kilometers" else "steps"
        return "$trainingType on ${Date(startTime.timeInMillis)} until ${Date(stopTime.timeInMillis)}, achieved: $detail $unit"
    }
}