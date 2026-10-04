package com.practicum.playlistmaker.ui.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.media.MediaActivity
import androidx.lifecycle.ViewModelProvider

class SearchActivity : AppCompatActivity() {

    private lateinit var viewModel: SearchViewModel
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter


    private lateinit var searchEditText: EditText
    private lateinit var linerNothingSearch: LinearLayout
    private lateinit var linerInternetProblem: LinearLayout
    private lateinit var linerSearchHistory: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var clearIcon: ImageView


    private var isClickAllowed = true
    private val clickHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel = ViewModelProvider(
            this,
            SearchViewModelFactory(
                Creator.provideSearchTracksInteractor(),
                Creator.provideSearchHistoryInteractor()
            )
        )[SearchViewModel::class.java]

        trackAdapter = TrackAdapter(emptyList()) { onTrackClicked(it) }
        historyAdapter = TrackAdapter(emptyList()) { onTrackClicked(it) }

        findViewById<RecyclerView>(R.id.recyclerViewTracks).apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = trackAdapter
        }
        findViewById<RecyclerView>(R.id.recyclerViewHistory).apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = historyAdapter
        }

        findViewById<ImageView>(R.id.search_back_button).setOnClickListener { finish() }

        searchEditText = findViewById(R.id.searchEditText)
        linerNothingSearch = findViewById(R.id.search_nothing_linear)
        linerInternetProblem = findViewById(R.id.search_internet_problem)
        linerSearchHistory = findViewById(R.id.search_history)
        progressBar = findViewById(R.id.search_progress_bar)
        clearIcon = findViewById<ImageView>(R.id.clearIcon)

        val updateButton = findViewById<Button>(R.id.search_button_update)
        val clearHistoryButton = findViewById<Button>(R.id.search_button_clear_history)

        searchEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.onSearchTextChanged(text?.toString().orEmpty())
        }

        searchEditText.setOnClickListener {
            searchEditText.requestFocus()
            (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
                .showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT)
        }

        updateButton.setOnClickListener { viewModel.onRetryClicked() }

        clearIcon.setOnClickListener {
            viewModel.onClearClicked()
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(searchEditText.windowToken, 0)
            searchEditText.clearFocus()
        }

        clearHistoryButton.setOnClickListener { viewModel.onClearHistoryClicked() }


        viewModel.state.observe(this) { state  ->
            if (searchEditText.text.toString() != state.searchText) {
                searchEditText.setText(state.searchText)
                searchEditText.setSelection(state.searchText.length)
            }

            clearIcon.visibility = if (state.searchText.isNotEmpty()) View.VISIBLE else View.GONE
            linerSearchHistory.visibility = if (state.showHistory) View.VISIBLE else View.GONE
            linerNothingSearch.visibility = if (state.showEmpty) View.VISIBLE else View.GONE
            linerInternetProblem.visibility = if (state.showError) View.VISIBLE else View.GONE
            progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE

            trackAdapter.submitList(state.tracks)
            historyAdapter.submitList(state.history)
        }

    }

    private fun onTrackClicked(track: Track) {
        if (!clickDebounce()) return
        viewModel.onTrackClicked(track)

        startActivity(Intent(this, MediaActivity::class.java).apply {
            putExtra(MediaActivity.EXTRA_TRACK, track)
        })
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
    }
    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}