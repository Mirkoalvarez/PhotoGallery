package com.example.photo.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.example.photo.R
import com.example.photo.core.ServiceLocator
import com.example.photo.core.UiState
import com.example.photo.databinding.FragmentHomeBinding
import com.example.photo.domain.model.Photo
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels {
        HomeViewModel.factory(ServiceLocator.provideRepository(requireContext()))
    }

    private lateinit var adapter: PhotoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = PhotoAdapter(
            onPhotoClick = { photo -> openDetail(photo) },
            onFavoriteClick = { photo -> viewModel.toggleFavorite(photo) }
        )
        binding.photoList.adapter = adapter
        binding.retryButton.setOnClickListener { viewModel.loadPhotos(forceRefresh = true) }

        addMenu()

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

    private fun addMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_home, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_settings -> {
                        findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
                        true
                    }
                    R.id.action_favorites -> {
                        findNavController().navigate(R.id.action_homeFragment_to_favoritesFragment)
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun renderState(state: UiState<List<Photo>>) {
        val showList = state is UiState.Success || (state is UiState.Loading && adapter.itemCount > 0)
        binding.loadingIndicator.isVisible = state is UiState.Loading && !showList
        binding.photoList.isVisible = showList
        binding.stateContainer.isVisible = state is UiState.Error || state is UiState.Empty
        binding.retryButton.isVisible = state is UiState.Error

        when (state) {
            is UiState.Success -> adapter.submitList(state.data)
            is UiState.Error -> binding.stateMessage.text =
                state.message.ifBlank { getString(R.string.state_error) }

            UiState.Empty -> binding.stateMessage.text = getString(R.string.state_empty)
            UiState.Loading -> binding.stateMessage.text = getString(R.string.home_loading_message)
        }
    }

    private fun openDetail(photo: Photo) {
        val args = bundleOf(ARG_PHOTO_ID to photo.id)
        findNavController().navigate(R.id.action_homeFragment_to_photoDetailFragment, args)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_PHOTO_ID = "photoId"
    }
}
