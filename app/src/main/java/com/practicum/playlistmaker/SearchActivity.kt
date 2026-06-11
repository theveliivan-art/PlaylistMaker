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
import org.w3c.dom.Text

class SearchActivity : AppCompatActivity() {

    private var userSearchText: String = ""
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