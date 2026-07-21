package com.practicum.playlistmaker

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.LinearLayout
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchActivity : AppCompatActivity(), TrackAdapter.OnHistoryChangeListener {

    private var userSearchText: String = ""
    private var tracks = mutableListOf<Track>()
    private var historySearch = mutableListOf<Track>()
    private val retrofit = Retrofit.Builder()
        .baseUrl(ITUNES_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val sharedPrefs by lazy {
        getSharedPreferences(PLAYLISTMAKER_PREFERENCES, MODE_PRIVATE)
    }
    private val gson = Gson()
    private val iTunesSearchService = retrofit.create(ITunesSearchAPI::class.java)
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var searchEditText: EditText
    private lateinit var linerNothingSearch: LinearLayout
    private lateinit var linerInternetProblem: LinearLayout

    private lateinit var linerSearchHistory: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        loadHistory()

        trackAdapter = TrackAdapter(tracks, historySearch, this)
        historyAdapter = TrackAdapter(historySearch, historySearch)

        val recyclerViewTracks = findViewById<RecyclerView>(R.id.recyclerViewTracks)
        recyclerViewTracks.layoutManager = LinearLayoutManager(this)
        recyclerViewTracks.adapter = trackAdapter

        val recyclerViewHistory = findViewById<RecyclerView>(R.id.recyclerViewHistory)
        recyclerViewHistory.layoutManager = LinearLayoutManager(this)
        recyclerViewHistory.adapter = historyAdapter

        val button_back_click = findViewById<ImageView>(R.id.search_back_button)
        button_back_click.setOnClickListener {
            finish()
        }

        searchEditText = findViewById(R.id.searchEditText)
        linerNothingSearch = findViewById<LinearLayout>(R.id.search_nothing_linear)
        linerInternetProblem = findViewById<LinearLayout>(R.id.search_internet_problem)
        linerSearchHistory = findViewById<LinearLayout>(R.id.search_history)

        val clearIcon = findViewById<ImageView>(R.id.clearIcon)
        val updateButton = findViewById<Button>(R.id.search_button_update)
        val clearHistoryButton = findViewById<Button>(R.id.search_button_clear_history)

        searchEditText.doOnTextChanged { text, start, before, count ->
            if (!text.isNullOrEmpty()){
                userSearchText = text.toString()
                clearIcon.visibility = View.VISIBLE
                linerSearchHistory.visibility = View.GONE
            }
            else {
                clearIcon.visibility = View.GONE
                linerSearchHistory.visibility = if(searchEditText.hasFocus()&& historySearch.isEmpty()) View.GONE else View.VISIBLE
            }
        }

        searchEditText.setOnClickListener {
            searchEditText.requestFocus()
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT)

        }
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                findTracks()
            }
            false
        }
        searchEditText.setOnFocusChangeListener{view, hasFocus ->
            linerSearchHistory.visibility = if (hasFocus && historySearch.isEmpty()) View.GONE else View.VISIBLE
        }

        updateButton.setOnClickListener {
            linerInternetProblem.visibility = View.GONE
            findTracks()
        }

        clearIcon.setOnClickListener {
            searchEditText.text.clear()
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(searchEditText.windowToken, 0)
            searchEditText.clearFocus()
            tracks.clear()
            trackAdapter.notifyDataSetChanged()
            historyAdapter.notifyDataSetChanged()
            linerNothingSearch.visibility = View.GONE
            linerInternetProblem.visibility = View.GONE
        }

        clearHistoryButton.setOnClickListener {
            historySearch.clear()
            saveHistory()
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
        val restoredText = savedInstanceState.getString(SEARCH_TEXT, "")
        if (restoredText.isNotEmpty()) {
            searchEditText.setText(restoredText)
        }
    }

    fun findTracks(){
        iTunesSearchService.search(userSearchText)
            .enqueue(object : Callback<TracksResponse> {
                override fun onResponse(call: Call<TracksResponse>,
                                        response: Response<TracksResponse>) {
                    if (response.isSuccessful){
                        tracks.clear()
                        val body = response.body()
                        if (body != null && body.results.isNotEmpty()) {
                            val convertedTracks = body.results.map { dto ->
                                val formattedTime = dateFormat.format(dto.trackTimeMillis)
                                Track(
                                    trackName = dto.trackName,
                                    artistName = dto.artistName,
                                    trackTime = formattedTime,
                                    artworkUrl100 = dto.artworkUrl100,
                                    trackId = dto.trackId
                                )
                            }
                            tracks.addAll(convertedTracks)
                            trackAdapter.notifyDataSetChanged()
                        }
                        if (tracks.isEmpty()){linerNothingSearch.visibility = View.VISIBLE}
                        else {linerNothingSearch.visibility = View.GONE}
                    }

                }

                override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                    linerInternetProblem.visibility = View.VISIBLE
                }

            })
    }

    override fun onHistoryChanged() {
        saveHistory()
        historyAdapter.notifyDataSetChanged()
    }
    fun saveHistory() {
        val json = gson.toJson(historySearch)
        sharedPrefs.edit().putString(HISTORY_SEARCH_KEY, json).apply()
    }

    private fun loadHistory() {
        val json = sharedPrefs.getString(HISTORY_SEARCH_KEY, null)
        if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<List<Track>>() {}.type
            val savedList: List<Track> = gson.fromJson(json, type)
            historySearch.clear()
            historySearch.addAll(savedList)
        }
    }

    companion object {
        const val SEARCH_TEXT = "PRODUCT_AMOUNT"
        private const val ITUNES_URL = "https://itunes.apple.com"
    }
}