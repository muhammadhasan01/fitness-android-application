package com.k310.fitness.trainingdb

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Training::class],
    version = 1
)
@TypeConverters(Converter::class)
abstract class TrainingDB : RoomDatabase() {

    abstract fun getTrainingDao(): TrainingDAO
}