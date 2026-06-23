package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Locale

class SearchActivity : AppCompatActivity() {


    private var currentSearchText: String = ""
    private var lastFailedSearchText: String = ""
    private var isSearchHistoryVisible: Boolean = false
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var iTunesApi: ITunesApi
    private lateinit var searchHistory: SearchHistory
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchHistoryContainer: LinearLayout
    private lateinit var searchHistoryTitle: TextView
    private lateinit var clearHistoryButton: Button
    private lateinit var nothingFound: LinearLayout
    private lateinit var noConnection: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        iTunesApi = Retrofit.Builder()
            .baseUrl(getString(R.string.base_itunes_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApi::class.java)

        searchHistory = SearchHistory(
            getSharedPreferences(SEARCH_HISTORY_PREFERENCES, Context.MODE_PRIVATE),
            Gson()
        )

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
            trackAdapter.setTracks(emptyList())
            hideSearchResults()
            hideKeyboard(searchEditText)
        }

        searchEditText.doOnTextChanged { text, _, _, _ ->
            currentSearchText = text?.toString().orEmpty()
            updateClearButtonVisibility(text)
            if (currentSearchText.isEmpty() && searchEditText.hasFocus()) {
                showSearchHistory()
            } else {
                hideSearchHistory()
            }
        }

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && currentSearchText.isEmpty()) {
                showSearchHistory()
            } else {
                hideSearchHistory()
            }
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTracks(currentSearchText)
                hideKeyboard(searchEditText)
                true
            } else {
                false
            }
        }

        if (savedInstanceState != null) {
            updateClearButtonVisibility(searchEditText.text)
        }

        recyclerView = findViewById(R.id.recyclerView)
        searchHistoryContainer = findViewById(R.id.searchHistory)
        searchHistoryTitle = findViewById(R.id.searchHistoryTitle)
        nothingFound = findViewById(R.id.nothingFound)
        noConnection = findViewById(R.id.noConnection)
        val refreshButton = findViewById<Button>(R.id.refreshButton)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)

        refreshButton.setOnClickListener {
            searchTracks(lastFailedSearchText)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)

        trackAdapter = TrackAdapter(mutableListOf()) { track ->
            if (!isSearchHistoryVisible) {
                searchHistory.addTrack(track)
            }
            openAudioPlayer(track)
        }
        recyclerView.adapter = trackAdapter

        clearHistoryButton.setOnClickListener {
            searchHistory.clearHistory()
            trackAdapter.setTracks(emptyList())
            hideSearchHistory()
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

    private fun searchTracks(text: String) {
        val query = text.trim()
        if (query.isEmpty()) {
            trackAdapter.setTracks(emptyList())
            hideSearchResults()
            return
        }

        hideSearchHistory()
        hideSearchResults()

        iTunesApi.search(query).enqueue(object : Callback<ITunesSearchResponse> {
            override fun onResponse(
                call: Call<ITunesSearchResponse>,
                response: Response<ITunesSearchResponse>
            ) {
                if (!response.isSuccessful) {
                    showSearchError(query)
                    return
                }

                val tracks = response.body()
                    ?.results
                    .orEmpty()
                    .mapNotNull { track ->
                        val trackId = track.trackId ?: return@mapNotNull null
                        Track(
                            trackId = trackId,
                            trackName = track.trackName.orEmpty(),
                            artistName = track.artistName.orEmpty(),
                            trackTime = formatTrackTime(track.trackTimeMillis ?: 0L),
                            artworkUrl100 = track.artworkUrl100.orEmpty(),
                            collectionName = track.collectionName,
                            releaseDate = track.releaseDate,
                            primaryGenreName = track.primaryGenreName.orEmpty(),
                            country = track.country.orEmpty()
                        )
                    }

                trackAdapter.setTracks(tracks)
                if (tracks.isEmpty()) {
                    showNothingFound()
                } else {
                    showSearchResults()
                }
            }

            override fun onFailure(call: Call<ITunesSearchResponse>, t: Throwable) {
                showSearchError(query)
            }
        })
    }

    private fun formatTrackTime(trackTimeMillis: Long): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)
    }

    private fun showSearchResults() {
        isSearchHistoryVisible = false
        searchHistoryContainer.visibility = View.VISIBLE
        searchHistoryTitle.visibility = View.GONE
        clearHistoryButton.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        nothingFound.visibility = View.GONE
        noConnection.visibility = View.GONE
    }

    private fun hideSearchResults() {
        searchHistoryContainer.visibility = View.GONE
        recyclerView.visibility = View.GONE
        nothingFound.visibility = View.GONE
        noConnection.visibility = View.GONE
    }

    private fun showNothingFound() {
        recyclerView.visibility = View.GONE
        searchHistoryContainer.visibility = View.GONE
        nothingFound.visibility = View.VISIBLE
        noConnection.visibility = View.GONE
    }

    private fun showSearchError(query: String) {
        lastFailedSearchText = query
        trackAdapter.setTracks(emptyList())
        recyclerView.visibility = View.GONE
        searchHistoryContainer.visibility = View.GONE
        nothingFound.visibility = View.GONE
        noConnection.visibility = View.VISIBLE
    }

    private fun showSearchHistory() {
        val history = searchHistory.getHistory()
        if (history.isEmpty()) {
            hideSearchHistory()
            return
        }

        isSearchHistoryVisible = true
        trackAdapter.setTracks(history)
        searchHistoryContainer.visibility = View.VISIBLE
        searchHistoryTitle.visibility = View.VISIBLE
        clearHistoryButton.visibility = View.VISIBLE
        recyclerView.visibility = View.VISIBLE
        nothingFound.visibility = View.GONE
        noConnection.visibility = View.GONE
    }

    private fun hideSearchHistory() {
        isSearchHistoryVisible = false
        searchHistoryContainer.visibility = View.GONE
    }

    private fun openAudioPlayer(track: Track) {
        val intent = Intent(this, AudioPlayer::class.java)
        intent.putExtra(AudioPlayer.TRACK_EXTRA, Gson().toJson(track))
        startActivity(intent)
    }

    companion object {
        private const val SEARCH_HISTORY_PREFERENCES = "search_history_preferences"
    }

}
