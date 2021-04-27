package com.k310.fitness.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.k310.fitness.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticViewModel @Inject constructor(
    val mainRepository: MainRepository
): ViewModel() {
}