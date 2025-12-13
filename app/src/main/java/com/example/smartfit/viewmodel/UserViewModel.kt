package com.example.smartfit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smartfit.data.database.User
import com.example.smartfit.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    // Loading states
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _isAuthenticating = MutableStateFlow(false)
    val isAuthenticating: StateFlow<Boolean> = _isAuthenticating.asStateFlow()

    val isLoggedIn: StateFlow<Boolean> = userRepository.isLoggedIn
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    // User Profile Data
    val userProfile: StateFlow<User?> = userRepository.userProfile
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    
    val name: StateFlow<String> = userProfile.map { it?.name ?: "User" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "User")
    
    val age: StateFlow<String> = userProfile.map { it?.age?.toString() ?: "25" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "25")
    
    val weight: StateFlow<String> = userProfile.map { it?.weight?.toString() ?: "70" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "70")
    
    val height: StateFlow<String> = userProfile.map { it?.height?.toString() ?: "170" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "170")
    
    val gender: StateFlow<String> = userProfile.map { it?.gender ?: "Not specified" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Not specified")

    fun login() {
        viewModelScope.launch {
            userRepository.login()
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }
    
    /**
     * Save user profile - replaces old data (no duplication)
     */
    fun saveProfile(name: String, age: String, weight: String, height: String, gender: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val ageInt = age.toIntOrNull() ?: 25
                val weightFloat = weight.toFloatOrNull() ?: 70f
                val heightFloat = height.toFloatOrNull() ?: 170f
                
                userRepository.saveUserProfile(
                    name = name,
                    age = ageInt,
                    weight = weightFloat,
                    height = heightFloat,
                    gender = gender
                )
            } catch (e: Exception) {
                // Handle error (could add error state if needed)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Initialize default user profile if none exists
     * Prevents crashes from missing user data
     */
    fun initializeDefaultProfile() {
        viewModelScope.launch {
            try {
                val existingUser = userRepository.getUser()
                if (existingUser == null) {
                    // Create default user profile
                    userRepository.saveUserProfile(
                        name = "User",
                        age = 25,
                        weight = 70f,
                        height = 170f,
                        gender = "Not specified"
                    )
                }
            } catch (e: Exception) {
                // Silently fail - user can set profile later
            }
        }
    }
    
    // ========== Authentication Methods ==========
    
    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()
    
    /**
     * Register new user with credentials and profile
     */
    fun register(
        username: String,
        email: String,
        password: String,
        name: String,
        age: String,
        weight: String,
        height: String,
        gender: String
    ) {
        viewModelScope.launch {
            _isAuthenticating.value = true
            try {
                // Save credentials
                userRepository.saveCredentials(username, email, password)
                
                // Save profile
                val ageInt = age.toIntOrNull() ?: 25
                val weightFloat = weight.toFloatOrNull() ?: 70f
                val heightFloat = height.toFloatOrNull() ?: 170f
                
                userRepository.saveUserProfile(
                    name = name,
                    age = ageInt,
                    weight = weightFloat,
                    height = heightFloat,
                    gender = gender
                )
                
                // Auto-login after registration
                userRepository.login()
                
                _loginError.value = null
            } catch (e: Exception) {
                _loginError.value = "Registration failed: ${e.message}"
            } finally {
                _isAuthenticating.value = false
            }
        }
    }
    
    /**
     * Validate credentials and login
     * Returns error message if validation fails
     */
    fun validateAndLogin(usernameOrEmail: String, password: String) {
        viewModelScope.launch {
            _isAuthenticating.value = true
            try {
                val isValid = userRepository.validateCredentials(usernameOrEmail, password)
                if (isValid) {
                    userRepository.login()
                    _loginError.value = null
                } else {
                    _loginError.value = "Invalid username/email or password"
                }
            } catch (e: Exception) {
                _loginError.value = "Login failed: ${e.message}"
            } finally {
                _isAuthenticating.value = false
            }
        }
    }
    
    /**
     * Clear login error
     */
    fun clearLoginError() {
        _loginError.value = null
    }
}

class UserViewModelFactory(
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}