package com.example.photo.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.photo.R
import com.example.photo.core.ServiceLocator
import com.example.photo.core.UiState
import com.example.photo.databinding.FragmentSettingsBinding
import com.example.photo.domain.model.CacheStatus
import com.google.android.material.snackbar.Snackbar
import java.text.DateFormat
import java.util.Date

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private var lastCacheCount: Int = -1

    private val viewModel: SettingsViewModel by viewModels {
        SettingsViewModel.factory(ServiceLocator.provideRepository(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.cacheMessage.text = getString(R.string.settings_cache_message)
        binding.clearCacheButton.setOnClickListener {
            viewModel.clearCache()
        }
        viewModel.cacheState.observe(viewLifecycleOwner) { renderState(it) }
    }

    private fun renderState(state: UiState<CacheStatus>) {
        when (state) {
            is UiState.Success -> {
                val status = state.data
                binding.cacheStatusValue.text =
                    getString(R.string.settings_cache_count, status.cachedPhotos)
                binding.cacheLastUpdate.text = formatLastSync(status.lastSyncTimestamp)
                binding.clearCacheButton.isEnabled = status.hasCache
                if (lastCacheCount > 0 && status.cachedPhotos == 0) {
                    Snackbar.make(binding.root, R.string.settings_cache_cleared, Snackbar.LENGTH_SHORT).show()
                }
                lastCacheCount = status.cachedPhotos
            }

            is UiState.Error -> {
                binding.cacheStatusValue.text = getString(R.string.settings_cache_error)
                binding.cacheLastUpdate.text = ""
                binding.clearCacheButton.isEnabled = false
                Snackbar.make(binding.root, R.string.settings_cache_error, Snackbar.LENGTH_SHORT).show()
            }

            UiState.Loading -> {
                binding.cacheStatusValue.text = getString(R.string.home_loading_message)
                binding.cacheLastUpdate.text = ""
                binding.clearCacheButton.isEnabled = false
            }

            UiState.Empty -> {
                binding.cacheStatusValue.text = getString(R.string.settings_cache_empty)
                binding.cacheLastUpdate.text = getString(R.string.settings_last_sync_never)
                binding.clearCacheButton.isEnabled = false
            }
        }
    }

    private fun formatLastSync(timestamp: Long): String {
        if (timestamp <= 0L) {
            return getString(R.string.settings_last_sync_never)
        }
        val dateText = DateFormat.getDateTimeInstance().format(Date(timestamp))
        return getString(R.string.settings_last_sync, dateText)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
