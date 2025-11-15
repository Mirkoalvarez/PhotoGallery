package com.example.photo.ui.favorites

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

class FavoritesViewModel(
    private val repository: PhotoRepository
) : ViewModel() {

    private val _favoritesState = MutableLiveData<UiState<List<Photo>>>(UiState.Loading)
    val favoritesState: LiveData<UiState<List<Photo>>> = _favoritesState

    private val _favoriteEvents = MutableLiveData<Event<Boolean?>>()
    val favoriteEvents: LiveData<Event<Boolean?>> = _favoriteEvents

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        _favoritesState.value = UiState.Loading
        viewModelScope.launch {
            repository.getFavoritePhotos()
                .onSuccess { photos ->
                    if (photos.isEmpty()) {
                        _favoritesState.value = UiState.Empty
                    } else {
                        _favoritesState.value = UiState.Success(photos)
                    }
                }
                .onFailure {
                    _favoritesState.value = UiState.Error(it.message.orEmpty())
                }
        }
    }

    fun toggleFavorite(photo: Photo) {
        viewModelScope.launch {
            repository.toggleFavorite(photo)
                .onSuccess { isFavorite ->
                    if (!isFavorite) {
                        removeFromList(photo.id)
                    } else {
                        refreshPhoto(photo.copy(isFavorite = isFavorite))
                    }
                    _favoriteEvents.value = Event(isFavorite)
                }
                .onFailure {
                    _favoriteEvents.value = Event(null)
                }
        }
    }

    private fun removeFromList(photoId: String) {
        val current = _favoritesState.value
        if (current is UiState.Success) {
            val updated = current.data.filterNot { it.id == photoId }
            _favoritesState.value = if (updated.isEmpty()) {
                UiState.Empty
            } else {
                UiState.Success(updated)
            }
        }
    }

    private fun refreshPhoto(photo: Photo) {
        val current = _favoritesState.value
        if (current is UiState.Success) {
            val updated = current.data.map { if (it.id == photo.id) photo else it }
            _favoritesState.value = UiState.Success(updated)
        }
    }

    companion object {
        fun factory(repository: PhotoRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
                        @Suppress("UNCHECKED_CAST")
                        return FavoritesViewModel(repository) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class $modelClass")
                }
            }
    }
}
