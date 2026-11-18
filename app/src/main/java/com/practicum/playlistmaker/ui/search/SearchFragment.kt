package com.practicum.playlistmaker.ui.search

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.media.MediaFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModel()

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private var isClickAllowed = true
    private val clickHandler = Handler(Looper.getMainLooper())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        trackAdapter = TrackAdapter(emptyList()) { onTrackClicked(it) }
        historyAdapter = TrackAdapter(emptyList()) { onTrackClicked(it) }

        binding.recyclerViewTracks.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = trackAdapter
        }
        binding.recyclerViewHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }

        binding.searchEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.onSearchTextChanged(text?.toString().orEmpty())
        }

        binding.searchEditText.setOnClickListener {
            binding.searchEditText.requestFocus()
            (requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .showSoftInput(binding.searchEditText, InputMethodManager.SHOW_IMPLICIT)
        }

        binding.searchButtonUpdate.setOnClickListener { viewModel.onRetryClicked() }

        binding.clearIcon.setOnClickListener {
            viewModel.onClearClicked()
            (requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
            binding.searchEditText.clearFocus()
        }

        binding.searchButtonClearHistory.setOnClickListener { viewModel.onClearHistoryClicked() }


        viewModel.state.observe(this) { state  ->
            if (binding.searchEditText.text.toString() != state.searchText) {
                binding.searchEditText.setText(state.searchText)
                binding.searchEditText.setSelection(state.searchText.length)
            }

            binding.clearIcon.visibility = if (state.searchText.isNotEmpty()) View.VISIBLE else View.GONE
            binding.searchHistory.visibility = if (state.showHistory) View.VISIBLE else View.GONE
            binding.searchNothingLinear.visibility = if (state.showEmpty) View.VISIBLE else View.GONE
            binding.searchInternetProblem.visibility = if (state.showError) View.VISIBLE else View.GONE
            binding.searchProgressBar.isVisible = state.isLoading

            trackAdapter.submitList(state.tracks)
            historyAdapter.submitList(state.history)
        }

    }

    private fun onTrackClicked(track: Track) {
        if (!clickDebounce()) return
        viewModel.onTrackClicked(track)

        val args = bundleOf(MediaFragment.EXTRA_TRACK to track)
        findNavController().navigate(R.id.action_searchFragment_to_mediaFragment, args)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            clickHandler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    override fun onDestroy() {
        super.onDestroy()
        clickHandler.removeCallbacksAndMessages(null)
        _binding = null
    }
    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}