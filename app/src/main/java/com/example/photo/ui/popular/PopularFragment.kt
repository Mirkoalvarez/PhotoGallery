package com.example.photo.ui.popular

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.photo.R
import com.example.photo.core.ServiceLocator
import com.example.photo.core.UiState
import com.example.photo.databinding.FragmentPopularBinding
import com.example.photo.domain.model.Photo
import com.example.photo.ui.home.HomeFragment
import com.example.photo.ui.home.PhotoAdapter
import com.google.android.material.snackbar.Snackbar

class PopularFragment : Fragment() {

    private var _binding: FragmentPopularBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PopularViewModel by viewModels {
        PopularViewModel.factory(ServiceLocator.provideRepository(requireContext()))
    }

    private lateinit var adapter: PhotoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPopularBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PhotoAdapter(
            onPhotoClick = { photo -> openDetail(photo) },
            onFavoriteClick = { photo -> viewModel.toggleFavorite(photo) }
        )
        binding.popularList.adapter = adapter

        binding.retryButton.setOnClickListener { viewModel.loadPopularPhotos(forceRefresh = true) }

        viewModel.photosState.observe(viewLifecycleOwner, ::renderState)
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

    private fun renderState(state: UiState<List<Photo>>) {
        val showList = state is UiState.Success || (state is UiState.Loading && adapter.itemCount > 0)
        binding.loadingIndicator.isVisible = state is UiState.Loading && !showList
        binding.popularList.isVisible = showList
        binding.stateContainer.isVisible = state is UiState.Error || state is UiState.Empty
        binding.retryButton.isVisible = state is UiState.Error

        when (state) {
            is UiState.Success -> adapter.submitList(state.data)
            is UiState.Error -> binding.stateMessage.text =
                state.message.ifBlank { getString(R.string.state_error) }

            UiState.Empty -> binding.stateMessage.text = getString(R.string.popular_state_empty)
            UiState.Loading -> binding.stateMessage.text = getString(R.string.popular_loading)
        }
    }

    private fun openDetail(photo: Photo) {
        val args = bundleOf(
            HomeFragment.ARG_PHOTO_ID to photo.id,
            HomeFragment.ARG_PHOTO to photo
        )
        findNavController().navigate(R.id.action_popularFragment_to_photoDetailFragment, args)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
