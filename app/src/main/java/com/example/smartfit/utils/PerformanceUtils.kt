package com.example.smartfit.utils

import androidx.compose.runtime.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

/**
 * Performance optimization utilities
 */

object PerformanceUtils {
    
    /**
     * Remember a value with a custom key to optimize recomposition
     */
    @Composable
    fun <T> rememberWithKey(key: Any, calculation: @DisallowComposableCalls () -> T): T {
        return remember(key) { calculation() }
    }
    
    /**
     * Remember a derived state to avoid unnecessary recompositions
     */
    @Composable
    fun <T> rememberDerivedState(
        calculation: @DisallowComposableCalls () -> T
    ): State<T> {
        return remember { derivedStateOf(calculation) }
    }
    
    /**
     * Collect a StateFlow with proper lifecycle awareness
     */
    @Composable
    fun <T> collectAsStateWithLifecycle(
        stateFlow: StateFlow<T>,
        initialValue: T
    ): State<T> {
        return produceState(initialValue) {
            stateFlow.collect { value = it }
        }
    }
    
    /**
     * Debounce rapid state changes
     */
    @Composable
    fun <T> debounceState(
        state: State<T>,
        delayMs: Int = 300
    ): State<T> {
        var debouncedValue by remember { mutableStateOf(state.value) }
        val coroutineScope = rememberCoroutineScope()
        
        LaunchedEffect(state.value) {
            coroutineScope.launch {
                kotlinx.coroutines.delay(delayMs.toLong())
                debouncedValue = state.value
            }
        }
        
        return remember { derivedStateOf { debouncedValue } }
    }
}

/**
 * Memory efficient data holder
 */
class EfficientDataHolder<T> {
    private var _data: T? = null
    
    fun get(): T? = _data
    
    fun set(data: T) {
        _data = data
    }
    
    fun clear() {
        _data = null
    }
    
    fun isSet(): Boolean = _data != null
}

/**
 * Lazy initialization utility
 */
class LazyInitializer<T>(
    private val initializer: () -> T
) {
    private var _value: T? = null
    private var isInitialized = false
    
    fun get(): T {
        if (!isInitialized) {
            _value = initializer()
            isInitialized = true
        }
        return _value!!
    }
}

/**
 * Resource manager for efficient resource usage
 */
class ResourceManager {
    private val resources = mutableMapOf<String, Any>()
    
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> get(key: String, factory: () -> T): T {
        return resources.getOrPut(key) { factory() } as T
    }
    
    fun clear() {
        resources.clear()
    }
    
    fun remove(key: String) {
        resources.remove(key)
    }
}