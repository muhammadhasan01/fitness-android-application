package com.k310.fitness.repository

import com.k310.fitness.db.schedule.Schedule
import com.k310.fitness.db.schedule.ScheduleDAO
import com.k310.fitness.db.training.Training
import com.k310.fitness.db.training.TrainingDAO
import javax.inject.Inject

class MainRepository @Inject constructor(
    val trainingDAO: TrainingDAO,
    val scheduleDAO: ScheduleDAO
) {
    // Training
    suspend fun insertTraining(training: Training) = trainingDAO.insert(training)
    suspend fun deleteTraining(training: Training) = trainingDAO.delete(training)

    fun getTrainingsByDate() = trainingDAO.getTrainingsByDate()
    fun getTrainingsByDistance() = trainingDAO.getTrainingsByDistance()
    fun getTrainingsByAvg() = trainingDAO.getTrainingsByAvg()
    fun getTrainingsByTime() = trainingDAO.getTrainingsByTime()

    fun getTotalTime() = trainingDAO.getTotalTime()
    fun getTotalDistance() = trainingDAO.getTotalDistance()
    fun getTotalAvg() = trainingDAO.getTotalAvg()

    // Schedule
    suspend fun insertSchedule(schedule: Schedule) = scheduleDAO.insert(schedule)
    suspend fun deleteSchedule(schedule: Schedule) = scheduleDAO.delete(schedule)

     fun getSchedules() = scheduleDAO.getSchedules()
}