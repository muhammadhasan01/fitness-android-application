package com.k310.fitness.repository

import com.k310.fitness.trainingdb.Training
import com.k310.fitness.trainingdb.TrainingDAO
import javax.inject.Inject

class MainRepository @Inject constructor(
    val trainingDAO: TrainingDAO
) {
    suspend fun insertTraining(training: Training) = trainingDAO.insertTraining(training)
    suspend fun deleteTraining(training: Training) = trainingDAO.deleteTraining(training)

    fun getTrainingsByDate() = trainingDAO.getTrainingsByDate()
    fun getTrainingsByDistance() = trainingDAO.getTrainingsByDistance()
    fun getTrainingsByAvg() = trainingDAO.getTrainingsByAvg()
    fun getTrainingsByTime() = trainingDAO.getTrainingsByTime()

    fun getTotalTime() = trainingDAO.getTotalTime()
    fun getTotalDistance() = trainingDAO.getTotalDistance()
    fun getTotalAvg() = trainingDAO.getTotalAvg()
}