package com.example.smartpotapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartpotapp.api.SensorData
import com.example.smartpotapp.repository.SensorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SensorViewModel : ViewModel() {
    private val repository = SensorRepository()
    
    private val _sensorData = MutableStateFlow<SensorData?>(null)
    val sensorData: StateFlow<SensorData?> = _sensorData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun fetchSensorData() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                _sensorData.value = repository.getSensorData()
            } catch (e: Exception) {
                _error.value = e.message ?: "Bir hata oluştu"
            } finally {
                _isLoading.value = false
            }
        }
    }
} 