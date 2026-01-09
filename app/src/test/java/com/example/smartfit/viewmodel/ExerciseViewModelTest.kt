package com.example.smartfit.viewmodel

import com.example.smartfit.data.network.dto.ExerciseDto
import com.example.smartfit.data.repository.ExerciseRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit Tests for ExerciseViewModel
 * Tests UI state logic including:
 * - Loading default data (Biceps)
 * - Handling body part selection
 * - Handling network errors
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ExerciseViewModelTest {

    private lateinit var viewModel: ExerciseViewModel
    private val repository: ExerciseRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ========== UIT-1-1: Initialization Tests ==========

    /**
     * Test that the ViewModel loads "biceps" exercises immediately when created.
     */
    @Test
    fun initLoadsBicepsExercisesByDefault() {
        // Arrange - Prepare a fake list of exercises
        val mockData = listOf(mockk<ExerciseDto>())
        mockRepositoryResponse(bodyPart = "biceps", result = Result.success(mockData))

        // Act - Initialize the ViewModel
        viewModel = ExerciseViewModel(repository)

        // Assert - Verify state matches the mock data
        assertEquals("Default body part should be biceps", "biceps", viewModel.selectedBodyPart.value)
        assertEquals("Exercises list should match repository data", mockData, viewModel.exercises.value)
    }

    // ========== UIT-1-2: Data Loading Tests ==========

    /**
     * Test selecting a new body part updates the list correctly.
     */
    @Test
    fun loadExercisesUpdatesListForNewBodyPart() {
        // Arrange - Setup initial state and new data
        setupViewModel() // Helper to init VM
        val cardioList = listOf(mockk<ExerciseDto>())
        mockRepositoryResponse(bodyPart = "cardio", result = Result.success(cardioList))

        // Act - User clicks "cardio"
        viewModel.loadExercises("cardio")

        // Assert
        assertEquals("Selected body part should update", "cardio", viewModel.selectedBodyPart.value)
        assertEquals("Exercise list should update to cardio", cardioList, viewModel.exercises.value)
    }

    /**
     * Test that if the network fails, the app handles it gracefully (empty list).
     */
    @Test
    fun loadExercisesHandlesNetworkErrorGracefully() {
        // Arrange
        setupViewModel()
        mockRepositoryResponse(bodyPart = "legs", result = Result.failure(Exception("Network Down")))

        // Act
        viewModel.loadExercises("legs")

        // Assert
        assertTrue("List should be empty on error", viewModel.exercises.value.isEmpty())
        assertFalse("Loading state should be false after error", viewModel.isLoading.value)
    }

    // ========== UIT-1-3: User Interaction Tests ==========

    /**
     * Test that clicking a specific exercise updates the detail view state.
     */
    @Test
    fun selectExerciseUpdatesSelectedState() {
        // Arrange
        setupViewModel()
        val targetExercise = mockk<ExerciseDto>()

        // Act
        viewModel.selectExercise(targetExercise)

        // Assert
        assertEquals("Selected exercise should match user input", targetExercise, viewModel.selectedExercise.value)
    }

    // ========== Helper Functions (Clean up the test code) ==========

    /**
     * Helper to initialize ViewModel with default mocks so tests don't crash.
     */
    private fun setupViewModel() {
        // We must mock the "biceps" call because init block calls it immediately
        mockRepositoryResponse("biceps", Result.success(emptyList()))
        viewModel = ExerciseViewModel(repository)
    }

    /**
     * Helper to mock the Repository behavior.
     * Replicates: "When repository.getExercises(X) is called, return Y"
     */
    private fun mockRepositoryResponse(bodyPart: String, result: Result<List<ExerciseDto>>) {
        coEvery { repository.getExercises(bodyPart) } returns flowOf(result)
    }
}