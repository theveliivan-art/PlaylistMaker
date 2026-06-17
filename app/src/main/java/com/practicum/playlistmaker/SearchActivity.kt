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
import org.w3c.dom.Text

class SearchActivity : AppCompatActivity() {

    private var userSearchText: String = ""
    private val tracks: MutableList<Track> = mutableListOf(
        Track("Smells Like Teen Spirit","Nirvana","5:01","https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"),
        Track("Billie Jean","Michael Jackson","4:35","https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"),
        Track("Stayin' Alive","Bee Gees","4:10","https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"),
        Track("Whole Lotta Love","Led Zeppelin","5:33","https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"),
        Track("Sweet Child O'Mine","Guns N' Roses","5:03","https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg")
    )
    private lateinit var searchEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerViewTracks = findViewById<RecyclerView>(R.id.recyclerViewTracks)
        recyclerViewTracks.layoutManager = LinearLayoutManager(this)
        val trackAdapter = TrackAdapter(tracks)
        recyclerViewTracks.adapter = trackAdapter

        val button_back_click = findViewById<ImageView>(R.id.search_back_button)
        button_back_click.setOnClickListener {
            finish()
        }

        searchEditText = findViewById(R.id.searchEditText)
        val clearIcon = findViewById<ImageView>(R.id.clearIcon)


        searchEditText.doOnTextChanged { text, start, before, count ->
            if (!text.isNullOrEmpty()){
                userSearchText = text.toString()
                clearIcon.visibility = View.VISIBLE
            }
            else {clearIcon.visibility = View.GONE}
        }

        searchEditText.setOnClickListener {
            searchEditText.requestFocus()
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT)

        }

        clearIcon.setOnClickListener {
            searchEditText.text.clear()
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(searchEditText.windowToken, 0)
            searchEditText.clearFocus()
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

    companion object {
        const val SEARCH_TEXT = "PRODUCT_AMOUNT"
    }
}