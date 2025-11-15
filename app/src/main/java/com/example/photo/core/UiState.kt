package com.example.photo.core

/**
 * Simple UI state used by the ViewModels to expose loading, data and error events.
 */
sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data object Empty : UiState<Nothing>()
    data class Error(val message: String) : UiState<Nothing>()
}
