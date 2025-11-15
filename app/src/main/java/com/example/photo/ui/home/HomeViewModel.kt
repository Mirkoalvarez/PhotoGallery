package com.example.photo.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.photo.core.Event
import com.example.photo.core.UiState
import com.example.photo.data.repository.PhotoRepository
import com.example.photo.domain.model.Photo
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: PhotoRepository
) : ViewModel() {

    private val _photosState = MutableLiveData<UiState<List<Photo>>>(UiState.Loading)
    val photosState: LiveData<UiState<List<Photo>>> = _photosState

    private val _favoriteEvents = MutableLiveData<Event<Boolean?>>()
    val favoriteEvents: LiveData<Event<Boolean?>> = _favoriteEvents

    init {
        loadPhotos()
    }

    fun loadPhotos(forceRefresh: Boolean = false) {
        _photosState.value = UiState.Loading
        viewModelScope.launch {
            repository.getLatestPhotos(forceRefresh)
                .onSuccess { photos ->
                    if (photos.isEmpty()) {
                        _photosState.value = UiState.Empty
                    } else {
                        _photosState.value = UiState.Success(photos)
                    }
                }
                .onFailure { throwable ->
                    _photosState.value = UiState.Error(throwable.message.orEmpty())
                }
        }
    }

    fun toggleFavorite(photo: Photo) {
        viewModelScope.launch {
            repository.toggleFavorite(photo)
                .onSuccess { isFavorite ->
                    val currentState = _photosState.value
                    if (currentState is UiState.Success) {
                        val updated = currentState.data.map { currentPhoto ->
                            if (currentPhoto.id == photo.id) {
                                currentPhoto.copy(isFavorite = isFavorite)
                            } else {
                                currentPhoto
                            }
                        }
                        _photosState.value = UiState.Success(updated)
                    }
                    _favoriteEvents.value = Event(isFavorite)
                }
                .onFailure {
                    _favoriteEvents.value = Event(null)
                }
        }
    }

    companion object {
        fun factory(repository: PhotoRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                        @Suppress("UNCHECKED_CAST")
                        return HomeViewModel(repository) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class $modelClass")
                }
            }
    }
}
