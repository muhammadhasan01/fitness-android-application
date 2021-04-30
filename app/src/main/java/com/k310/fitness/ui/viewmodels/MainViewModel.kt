package com.k310.fitness.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.k310.fitness.db.schedule.Schedule
import com.k310.fitness.db.training.Training
import com.k310.fitness.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val mainRepository: MainRepository
) : ViewModel() {
    // Training
    fun insertTraining(training: Training) = viewModelScope.launch {
        mainRepository.insertTraining(training)
    }

    fun deleteTraining(training: Training) = viewModelScope.launch {
        mainRepository.deleteTraining(training)
    }

    fun getTrainingsByDate() = mainRepository.getTrainingsByDate()
    fun getTrainingsByDistance() = mainRepository.getTrainingsByDistance()
    fun getTrainingsByAvg() = mainRepository.getTrainingsByAvg()
    fun getTrainingsByTime() = mainRepository.getTrainingsByTime()

    fun getTotalTime() = mainRepository.getTotalTime()
    fun getTotalDistance() = mainRepository.getTotalDistance()
    fun getTotalAvg() = mainRepository.getTotalAvg()

    // Schedules
    fun insertSchedule(schedule: Schedule) = viewModelScope.launch {
        mainRepository.insertSchedule(schedule)
    }

    fun deleteSchedule(schedule: Schedule) = viewModelScope.launch {
        mainRepository.deleteSchedule(schedule)
    }

    fun getSchedules() = mainRepository.getSchedules()
}