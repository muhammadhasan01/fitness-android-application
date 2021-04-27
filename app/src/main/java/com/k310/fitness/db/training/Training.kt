package com.k310.fitness.db.training

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "training_table")
data class Training(
    var img: Bitmap? = null,
    var timestamp: Long = 0L,
    var avgInKMH: Float = 0f,
    var distanceInMeters: Int = 0,
    var timeInMs: Long = 0L
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}