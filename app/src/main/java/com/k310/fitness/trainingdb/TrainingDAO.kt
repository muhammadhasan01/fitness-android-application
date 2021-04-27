package com.k310.fitness.trainingdb

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TrainingDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTraining(training: Training)

    @Delete
    suspend fun deleteTraining(training: Training)

    @Query("SELECT * FROM training_table ORDER BY timestamp DESC")
    fun getTrainingsByDate(): LiveData<List<Training>>

    @Query("SELECT * FROM training_table ORDER BY avgInKMH DESC")
    fun getTrainingsByAvg(): LiveData<List<Training>>

    @Query("SELECT * FROM training_table ORDER BY timeInMs DESC")
    fun getTrainingsByTime(): LiveData<List<Training>>

    @Query("SELECT * FROM training_table ORDER BY distanceInMeters DESC")
    fun getTrainingsByDistance(): LiveData<List<Training>>

    @Query("SELECT SUM(timeInMs) FROM training_table")
    fun getTotalTime(): LiveData<Long>

    @Query("SELECT SUM(distanceInMeters) FROM training_table")
    fun getTotalDistance(): LiveData<Int>

    @Query("SELECT AVG(avgInKMH) FROM training_table")
    fun getTotalAvg(): LiveData<Float>
}