package com.example.photo.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.photo.core.UiState
import com.example.photo.data.repository.PhotoRepository
import com.example.photo.domain.model.CacheStatus
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: PhotoRepository
) : ViewModel() {

    private val _cacheState = MutableLiveData<UiState<CacheStatus>>(UiState.Loading)
    val cacheState: LiveData<UiState<CacheStatus>> = _cacheState

    init {
        refreshStatus()
    }

    fun refreshStatus() {
        _cacheState.value = UiState.Loading
        viewModelScope.launch {
            repository.getCacheStatus()
                .onSuccess { status ->
                    _cacheState.value = UiState.Success(status)
                }
                .onFailure { throwable ->
                    _cacheState.value = UiState.Error(throwable.message.orEmpty())
                }
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            repository.clearCache()
                .onSuccess { refreshStatus() }
                .onFailure { throwable ->
                    _cacheState.value = UiState.Error(throwable.message.orEmpty())
                }
        }
    }

    companion object {
        fun factory(repository: PhotoRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
                        @Suppress("UNCHECKED_CAST")
                        return SettingsViewModel(repository) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class $modelClass")
                }
            }
    }
}
