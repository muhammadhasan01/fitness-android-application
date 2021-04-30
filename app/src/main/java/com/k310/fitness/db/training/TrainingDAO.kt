package com.k310.fitness.db.training

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.k310.fitness.db.BaseDao

@Dao
interface TrainingDAO : BaseDao<Training> {
    @Query("SELECT * FROM training_table ORDER BY startTime DESC")
    fun getTrainingsByDate(): LiveData<List<Training>>

    @Query("SELECT * FROM training_table WHERE startTime BETWEEN :dayInMs AND :nextDayInMs")
    fun getTrainingsByDate(dayInMs: Long, nextDayInMs: Long): LiveData<List<Training>>

    @Query("SELECT * FROM training_table ORDER BY avgInKMH DESC")
    fun getTrainingsByAvg(): LiveData<List<Training>>

    @Query("SELECT * FROM training_table ORDER BY duration DESC")
    fun getTrainingsByTime(): LiveData<List<Training>>

    @Query("SELECT * FROM training_table ORDER BY detail DESC")
    fun getTrainingsByDistance(): LiveData<List<Training>>

    @Query("SELECT SUM(duration) FROM training_table")
    fun getTotalTime(): LiveData<Long>

    @Query("SELECT SUM(detail) FROM training_table")
    fun getTotalDistance(): LiveData<Int>

    @Query("SELECT AVG(avgInKMH) FROM training_table")
    fun getTotalAvg(): LiveData<Float>
}