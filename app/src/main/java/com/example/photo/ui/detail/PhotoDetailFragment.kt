package com.example.photo.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.BundleCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.photo.R
import com.example.photo.core.ServiceLocator
import com.example.photo.core.UiState
import com.example.photo.databinding.FragmentPhotoDetailBinding
import com.example.photo.domain.model.Photo
import com.example.photo.ui.home.HomeFragment
import coil.load
import com.google.android.material.snackbar.Snackbar

class PhotoDetailFragment : Fragment() {

    private var _binding: FragmentPhotoDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PhotoDetailViewModel by viewModels {
        val repository = ServiceLocator.provideRepository(requireContext())
        val args = arguments
        val photoId = args?.getString(HomeFragment.ARG_PHOTO_ID).orEmpty()
        val initialPhoto = args?.let {
            BundleCompat.getParcelable(it, HomeFragment.ARG_PHOTO, Photo::class.java)
        }
        PhotoDetailViewModel.factory(repository, photoId, initialPhoto)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.detailMessage.setOnClickListener { viewModel.loadPhoto() }
        binding.toggleFavoriteButton.setOnClickListener { viewModel.toggleFavorite() }
        viewModel.photoState.observe(viewLifecycleOwner) { renderState(it) }
        viewModel.favoriteEvents.observe(viewLifecycleOwner) { event ->
            event?.getContentIfNotHandled()?.let { result ->
                val messageRes = when (result) {
                    true -> R.string.message_favorite_added
                    false -> R.string.message_favorite_removed
                    null -> R.string.message_favorite_error
                }
                Snackbar.make(binding.root, getString(messageRes), Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun renderState(state: UiState<Photo>) {
        binding.detailProgress.isVisible = state is UiState.Loading
        val isSuccess = state is UiState.Success
        binding.detailScroll.isVisible = isSuccess
        binding.toggleFavoriteButton.isVisible = isSuccess
        binding.toggleFavoriteButton.isEnabled = isSuccess
        val showMessage = state is UiState.Error || state is UiState.Empty
        binding.detailMessage.isVisible = showMessage
        binding.detailMessage.isClickable = state is UiState.Error

        when (state) {
            is UiState.Success -> showPhoto(state.data)
            is UiState.Error -> binding.detailMessage.text =
                state.message.ifBlank { getString(R.string.detail_missing_photo) }

            UiState.Empty -> binding.detailMessage.text = getString(R.string.detail_missing_photo)
            UiState.Loading -> binding.detailMessage.text = getString(R.string.home_loading_message)
        }
    }

    private fun showPhoto(photo: Photo) {
        binding.detailPhoto.load(photo.fullUrl) {
            crossfade(true)
        }
        binding.detailAuthor.text = photo.authorName
        binding.detailDescription.text = photo.description.ifBlank {
            getString(R.string.detail_no_description)
        }
        binding.detailLikes.text = getString(R.string.photo_detail_likes, photo.likes)
        binding.detailDimensions.text =
            getString(R.string.photo_detail_dimensions, photo.resolutionText)
        val iconRes = if (photo.isFavorite) {
            R.drawable.ic_favorite_filled
        } else {
            R.drawable.ic_favorite_outline
        }
        val textRes = if (photo.isFavorite) {
            R.string.action_unlike
        } else {
            R.string.action_like
        }
        binding.toggleFavoriteButton.setIconResource(iconRes)
        binding.toggleFavoriteButton.text = getString(textRes)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
