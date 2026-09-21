package com.practicum.playlistmaker.presentation

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
import com.practicum.playlistmaker.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.api.SearchTracksInteractor
import com.practicum.playlistmaker.domain.models.Track

class SearchActivity : AppCompatActivity() {

    private var userSearchText: String = ""
    private val tracks = mutableListOf<Track>()
    private val historySearch = mutableListOf<Track>()

    private lateinit var searchTracksInteractor: SearchTracksInteractor
    private lateinit var searchHistoryInteractor: SearchHistoryInteractor

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private lateinit var searchEditText: EditText
    private lateinit var linerNothingSearch: LinearLayout
    private lateinit var linerInternetProblem: LinearLayout
    private lateinit var linerSearchHistory: LinearLayout
    private lateinit var progressBar: ProgressBar

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { findTracks() }

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

        searchTracksInteractor = Creator.provideSearchTracksInteractor()
        searchHistoryInteractor = Creator.provideSearchHistoryInteractor(this)

        historySearch.addAll(searchHistoryInteractor.getHistory())

        trackAdapter = TrackAdapter(tracks) { onTrackClicked(it) }
        historyAdapter = TrackAdapter(historySearch) { onTrackClicked(it) }

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

        val clearIcon = findViewById<ImageView>(R.id.clearIcon)
        val updateButton = findViewById<Button>(R.id.search_button_update)
        val clearHistoryButton = findViewById<Button>(R.id.search_button_clear_history)

        searchEditText.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrEmpty()) {
                userSearchText = text.toString()
                clearIcon.visibility = View.VISIBLE
                linerSearchHistory.visibility = View.GONE
                handler.removeCallbacks(searchRunnable)
                handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
            } else {
                clearIcon.visibility = View.GONE
                linerSearchHistory.visibility =
                    if (searchEditText.hasFocus() && historySearch.isEmpty()) View.GONE else View.VISIBLE
                handler.removeCallbacks(searchRunnable)
                progressBar.visibility = View.GONE
            }
        }

        searchEditText.setOnClickListener {
            searchEditText.requestFocus()
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT)
        }

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            linerSearchHistory.visibility =
                if (hasFocus && historySearch.isEmpty()) View.GONE else View.VISIBLE
        }

        updateButton.setOnClickListener {
            linerInternetProblem.visibility = View.GONE
            findTracks()
        }

        clearIcon.setOnClickListener {
            searchEditText.text.clear()
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(searchEditText.windowToken, 0)
            searchEditText.clearFocus()
            tracks.clear()
            trackAdapter.notifyDataSetChanged()
            historyAdapter.notifyDataSetChanged()
            linerNothingSearch.visibility = View.GONE
            linerInternetProblem.visibility = View.GONE
            progressBar.visibility = View.GONE
            handler.removeCallbacks(searchRunnable)
        }

        clearHistoryButton.setOnClickListener {
            searchHistoryInteractor.clearHistory()
            historySearch.clear()
            historyAdapter.notifyDataSetChanged()
            linerSearchHistory.visibility = View.GONE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, userSearchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val restored = savedInstanceState.getString(SEARCH_TEXT, "")
        if (restored.isNotEmpty()) searchEditText.setText(restored)
    }

    private fun findTracks() {
        linerInternetProblem.visibility = View.GONE
        linerNothingSearch.visibility = View.GONE
        progressBar.visibility = View.VISIBLE

        searchTracksInteractor.searchTracks(
            userSearchText,
            onSuccess = { result ->
                progressBar.visibility = View.GONE
                tracks.clear()
                tracks.addAll(result)
                trackAdapter.notifyDataSetChanged()
                linerNothingSearch.visibility = if (tracks.isEmpty()) View.VISIBLE else View.GONE
            },
            onError = {
                progressBar.visibility = View.GONE
                linerInternetProblem.visibility = View.VISIBLE
            }
        )
    }

    private fun onTrackClicked(track: Track) {
        if (!clickDebounce()) return
        searchHistoryInteractor.addTrack(track)
        historySearch.clear()
        historySearch.addAll(searchHistoryInteractor.getHistory())
        historyAdapter.notifyDataSetChanged()

        startActivity(Intent(this, MediaActivity::class.java).apply {
            putExtra("track", track)
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

    companion object {
        const val SEARCH_TEXT = "PRODUCT_AMOUNT"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}