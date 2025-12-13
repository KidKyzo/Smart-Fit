package com.example.smartfit.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smartfit.data.database.AppDatabase
import com.example.smartfit.data.database.FoodIntakeLog
import com.example.smartfit.data.model.FoodData
import com.example.smartfit.data.model.MockFoodData
import com.example.smartfit.data.network.NetworkModule
import com.example.smartfit.data.repository.FoodIntakeRepository
import com.example.smartfit.data.repository.FoodRepository
import com.example.smartfit.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for Food Search and Intake Tracking
 * Integrates FatSecret API with local database
 */
class FoodViewModel(
    private val foodRepository: FoodRepository,
    private val foodIntakeRepository: FoodIntakeRepository
) : ViewModel() {
    
    // Search state
    private val _searchResults = MutableStateFlow<List<FoodData>>(emptyList())
    val searchResults: StateFlow<List<FoodData>> = _searchResults.asStateFlow()
    
    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()
    
    private val _searchError = MutableStateFlow<String?>(null)
    val searchError: StateFlow<String?> = _searchError.asStateFlow()
    
    // Today's food intake
    val todayFoodIntake = foodIntakeRepository.getTodayFoodIntake()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    // Today's total calorie intake
    private val _todayCalorieIntake = MutableStateFlow(0)
    val todayCalorieIntake: StateFlow<Int> = _todayCalorieIntake.asStateFlow()
    
    init {
        loadTodayCalories()
    }
    
    /**
     * Search for foods by name
     * Falls back to MockFoodData if API fails (e.g., IP restriction)
     */
    fun searchFoods(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        
        viewModelScope.launch {
            _isSearching.value = true
            _searchError.value = null
            
            val result = foodRepository.searchFoods(query)
            
            result.onSuccess { foods ->
                if (foods.isNotEmpty()) {
                    // API returned results
                    _searchResults.value = foods
                    Log.d("FoodViewModel", "API search successful: ${foods.size} foods")
                } else {
                    // API returned empty, use mock data
                    Log.w("FoodViewModel", "API returned empty, using MockFoodData")
                    _searchResults.value = MockFoodData.getFoods().filter { 
                        it.title.contains(query, ignoreCase = true) 
                    }
                }
            }.onFailure { error ->
                // API failed (IP restriction, network error, etc.), use mock data
                Log.e("FoodViewModel", "API search failed, using MockFoodData", error)
                _searchError.value = "Using sample data (API: ${error.message})"
                _searchResults.value = MockFoodData.getFoods().filter { 
                    it.title.contains(query, ignoreCase = true) 
                }
            }
            
            _isSearching.value = false
        }
    }
    
    /**
     * Log food intake
     */
    fun logFood(
        food: FoodData,
        servings: Float = 1.0f,
        mealType: String = "Snack"
    ) {
        viewModelScope.launch {
            val foodIntake = FoodIntakeLog(
                userId = 0,  // Will be set by repository
                foodId = food.id.toString(),
                foodName = food.title,
                calories = food.calories,
                protein = food.protein.replace("g", "").toFloatOrNull() ?: 0f,
                carbs = food.carbs.replace("g", "").toFloatOrNull() ?: 0f,
                fats = food.fats.replace("g", "").toFloatOrNull() ?: 0f,
                servingSize = food.servingSize,
                servings = servings,
                date = System.currentTimeMillis(),
                mealType = mealType
            )
            
            foodIntakeRepository.logFoodIntake(foodIntake)
            loadTodayCalories()
        }
    }
    
    /**
     * Delete food intake log
     */
    fun deleteFoodIntake(foodIntake: FoodIntakeLog) {
        viewModelScope.launch {
            foodIntakeRepository.deleteFoodIntake(foodIntake)
            loadTodayCalories()
        }
    }
    
    /**
     * Load today's total calories
     */
    private fun loadTodayCalories() {
        viewModelScope.launch {
            _todayCalorieIntake.value = foodIntakeRepository.getTodayTotalCalories()
        }
    }
    
    /**
     * Clear search results
     */
    fun clearSearch() {
        _searchResults.value = emptyList()
        _searchError.value = null
    }
}

/**
 * Factory for creating FoodViewModel with dependencies
 */
class FoodViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FoodViewModel::class.java)) {
            val database = AppDatabase.getDatabase(application)
            val userRepository = UserRepository(application)
            
            val foodRepository = FoodRepository(
                api = NetworkModule.provideFatSecretApi()
            )
            
            val foodIntakeRepository = FoodIntakeRepository(
                foodIntakeDao = database.foodIntakeDao(),
                userRepository = userRepository
            )
            
            @Suppress("UNCHECKED_CAST")
            return FoodViewModel(foodRepository, foodIntakeRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
