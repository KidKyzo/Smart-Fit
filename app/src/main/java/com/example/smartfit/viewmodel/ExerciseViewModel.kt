package com.example.smartfit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smartfit.data.network.dto.ExerciseDto
import com.example.smartfit.data.repository.ExerciseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExerciseViewModel(
    private val repository: ExerciseRepository
) : ViewModel() {

    private val _exercises = MutableStateFlow<List<ExerciseDto>>(emptyList())
    val exercises: StateFlow<List<ExerciseDto>> = _exercises.asStateFlow()

    private val _selectedExercise = MutableStateFlow<ExerciseDto?>(null)
    val selectedExercise: StateFlow<ExerciseDto?> = _selectedExercise.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedBodyPart = MutableStateFlow("All")
    val selectedBodyPart: StateFlow<String> = _selectedBodyPart.asStateFlow()

    init {
        // Initial load
        loadExercises("biceps")
    }

    fun loadExercises(bodyPart: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _selectedBodyPart.value = bodyPart

            repository.getExercises(bodyPart)
                .collect { result ->
                    result.onSuccess { list -> _exercises.value = list }
                    result.onFailure { _exercises.value = emptyList() }
                    _isLoading.value = false
                }
        }
    }

    // Since this API doesn't have ID lookup, we just set the object directly
    fun selectExercise(exercise: ExerciseDto) {
        _selectedExercise.value = exercise
    }
}

class ExerciseViewModelFactory(private val repository: ExerciseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExerciseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExerciseViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}