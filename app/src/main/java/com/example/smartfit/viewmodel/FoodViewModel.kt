package com.example.smartfit.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smartfit.data.database.AppDatabase
import com.example.smartfit.data.database.FoodIntakeLog
import com.example.smartfit.data.model.FoodData
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
    
    // Pagination state
    private val _currentPage = MutableStateFlow(0)
    private val _hasMore = MutableStateFlow(true)
    val hasMore: StateFlow<Boolean> = _hasMore.asStateFlow()
    
    private var currentQuery = ""
    
    // Sort state
    enum class SortOption {
        LOWEST_CALORIES,
        HIGHEST_CALORIES,
        A_TO_Z,
        Z_TO_A
    }
    
    private val _sortOption = MutableStateFlow(SortOption.A_TO_Z)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()
    
    // Today's food intake
    val todayFoodIntake = foodIntakeRepository.getTodayFoodIntake()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    // Today's total calorie intake
    private val _todayCalorieIntake = MutableStateFlow(0)
    val todayCalorieIntake: StateFlow<Int> = _todayCalorieIntake.asStateFlow()
    
    companion object {
        private const val ITEMS_PER_PAGE = 20
    }
    
    // Default search queries for Indonesian and Malaysian foods
    private val defaultSearchQueries = listOf(
        "nasi goreng",
        "rendang",
        "satay",
        "nasi lemak",
        "mie goreng"
    )
    private var defaultQueryIndex = 0
    
    init {
        loadTodayCalories()
        // Load default Indonesian/Malaysian foods
        searchFoods(defaultSearchQueries[0])
    }
    
    /**
     * Search for foods using FatSecret API
     * Loads first page and removes duplicates
     */
    fun searchFoods(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        
        currentQuery = query
        
        viewModelScope.launch {
            _isSearching.value = true
            _searchError.value = null
            _currentPage.value = 0
            _hasMore.value = true
            
            val result = foodRepository.searchFoods(query, page = 0)
            
            result.onSuccess { foods ->
                // Remove duplicates by foodId
                val uniqueFoods = foods.distinctBy { it.id }
                
                // Further deduplicate by food name (merge same foods with different servings)
                val deduplicatedFoods = uniqueFoods.groupBy { it.title }
                    .map { (_, foodList) ->
                        // Take first food as base, it already has default serving options
                        foodList.first()
                    }
                
                // Apply sorting
                val sortedFoods = applySorting(deduplicatedFoods)
                
                // Take first page
                _searchResults.value = sortedFoods.take(ITEMS_PER_PAGE)
                
                // Check if there are more results
                _hasMore.value = sortedFoods.size > ITEMS_PER_PAGE
                
                Log.d("FoodViewModel", "API search successful: ${uniqueFoods.size} unique foods, ${deduplicatedFoods.size} after deduplication, showing ${_searchResults.value.size}")
            }.onFailure { error ->
                Log.e("FoodViewModel", "API search failed", error)
                _searchError.value = error.message ?: "Failed to search foods"
                _searchResults.value = emptyList()
                _hasMore.value = false
            }
            
            _isSearching.value = false
        }
    }
    
    /**
     * Load more results (pagination)
     * Fetches next page from API
     */
    fun loadMore() {
        if (!_hasMore.value || _isSearching.value || currentQuery.isBlank()) return
        
        viewModelScope.launch {
            _isSearching.value = true
            
            val nextPage = _currentPage.value + 1
            val result = foodRepository.searchFoods(currentQuery, page = nextPage)
            
            result.onSuccess { newFoods ->
                if (newFoods.isEmpty()) {
                    // No more results
                    _hasMore.value = false
                } else {
                    // Remove duplicates
                    val uniqueNewFoods = newFoods.distinctBy { it.id }
                    
                    // Combine with existing results
                    val allFoods = (_searchResults.value + uniqueNewFoods).distinctBy { it.id }
                    
                    // Apply sorting
                    val sortedFoods = applySorting(allFoods)
                    
                    _searchResults.value = sortedFoods
                    _currentPage.value = nextPage
                    
                    // Check if we got less than expected (means no more pages)
                    _hasMore.value = uniqueNewFoods.size >= ITEMS_PER_PAGE
                    
                    Log.d("FoodViewModel", "Loaded page $nextPage: ${uniqueNewFoods.size} new foods, total: ${sortedFoods.size}")
                }
            }.onFailure { error ->
                Log.e("FoodViewModel", "Load more failed", error)
                _hasMore.value = false
            }
            
            _isSearching.value = false
        }
    }
    
    /**
     * Change sort option
     */
    fun setSortOption(option: SortOption) {
        _sortOption.value = option
        
        // Re-sort existing results
        val sortedFoods = applySorting(_searchResults.value)
        _searchResults.value = sortedFoods
    }
    
    /**
     * Apply sorting to food list
     */
    private fun applySorting(foods: List<FoodData>): List<FoodData> {
        return when (_sortOption.value) {
            SortOption.LOWEST_CALORIES -> foods.sortedBy { it.calories }
            SortOption.HIGHEST_CALORIES -> foods.sortedByDescending { it.calories }
            SortOption.A_TO_Z -> foods.sortedBy { it.title }
            SortOption.Z_TO_A -> foods.sortedByDescending { it.title }
        }
    }
    
    /**
     * Reset search and sort to defaults
     */
    fun resetSearchAndSort() {
        currentQuery = ""
        _searchResults.value = emptyList()
        _searchError.value = null
        _currentPage.value = 0
        _hasMore.value = true
        _sortOption.value = SortOption.A_TO_Z
        _isSearching.value = false
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
     * Get calories for a specific date
     */
    suspend fun getCaloriesForDate(date: Long): Int {
        return foodIntakeRepository.getCaloriesForDate(date)
    }
    
    /**
     * Clear search results
     */
    fun clearSearch() {
        _searchResults.value = emptyList()
        _searchError.value = null
        _currentPage.value = 0
        _hasMore.value = true
    }
    
    /**
     * Undo delete food intake (re-insert)
     */
    fun undoDeleteFoodIntake(foodIntake: FoodIntakeLog) {
        viewModelScope.launch {
            foodIntakeRepository.reinsertFoodIntake(foodIntake)
            _todayCalorieIntake.value = foodIntakeRepository.getTodayTotalCalories()
        }
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
