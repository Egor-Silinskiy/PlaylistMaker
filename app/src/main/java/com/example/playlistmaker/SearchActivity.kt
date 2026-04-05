package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged

class SearchActivity : AppCompatActivity() {


    private var currentSearchText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            val settingsIntent = Intent(this, MainActivity::class.java)
            startActivity(settingsIntent)
        }

        /* кнопка сброса */
        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        val clearImage = findViewById<ImageView>(R.id.clearImage)

        fun updateClearButtonVisibility(s: CharSequence?) {
            clearImage.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
        }

        clearImage.setOnClickListener {
            searchEditText.text.clear()
            hideKeyboard(searchEditText)
        }

        searchEditText.doOnTextChanged { text, _, _, _ ->
            currentSearchText = text?.toString().orEmpty()
            updateClearButtonVisibility(text)
        }

        if (savedInstanceState != null) {
            updateClearButtonVisibility(searchEditText.text)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(Constants.SEARCH_KEY, currentSearchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val restoredText = savedInstanceState.getString(Constants.SEARCH_KEY, "")
        findViewById<EditText>(R.id.searchEditText).setText(restoredText)
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        view.clearFocus()
    }

}