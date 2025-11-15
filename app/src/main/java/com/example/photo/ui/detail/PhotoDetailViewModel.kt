package com.example.photo.ui.detail

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

class PhotoDetailViewModel(
    private val repository: PhotoRepository,
    private val photoId: String
) : ViewModel() {

    private val _photoState = MutableLiveData<UiState<Photo>>(UiState.Loading)
    val photoState: LiveData<UiState<Photo>> = _photoState

    private val _favoriteEvents = MutableLiveData<Event<Boolean?>>()
    val favoriteEvents: LiveData<Event<Boolean?>> = _favoriteEvents

    init {
        loadPhoto()
    }

    fun loadPhoto() {
        if (photoId.isBlank()) {
            _photoState.value = UiState.Error("Missing photo id")
            return
        }
        _photoState.value = UiState.Loading
        viewModelScope.launch {
            repository.getPhoto(photoId)
                .onSuccess { photo ->
                    _photoState.value = UiState.Success(photo)
                }
                .onFailure { throwable ->
                    _photoState.value = UiState.Error(throwable.message.orEmpty())
                }
        }
    }

    fun toggleFavorite() {
        val currentPhoto = (_photoState.value as? UiState.Success)?.data ?: return
        viewModelScope.launch {
            repository.toggleFavorite(currentPhoto)
                .onSuccess { isFavorite ->
                    _photoState.value = UiState.Success(currentPhoto.copy(isFavorite = isFavorite))
                    _favoriteEvents.value = Event(isFavorite)
                }
                .onFailure {
                    _favoriteEvents.value = Event(null)
                }
        }
    }

    companion object {
        fun factory(repository: PhotoRepository, photoId: String): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(PhotoDetailViewModel::class.java)) {
                        @Suppress("UNCHECKED_CAST")
                        return PhotoDetailViewModel(repository, photoId) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class $modelClass")
                }
            }
    }
}
