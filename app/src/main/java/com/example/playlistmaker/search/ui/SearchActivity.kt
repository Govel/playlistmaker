//package com.example.playlistmaker.search.ui
//
//import android.annotation.SuppressLint
//import android.content.Intent
//import android.os.Bundle
//import android.os.Handler
//import android.os.Looper
//import android.text.Editable
//import android.text.TextWatcher
//import android.view.inputmethod.EditorInfo
//import android.view.inputmethod.InputMethodManager
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import androidx.core.view.isVisible
//import com.example.playlistmaker.R
//import com.example.playlistmaker.databinding.ActivitySearchBinding
//import com.example.playlistmaker.player.ui.AudioPlayerActivity
//import com.example.playlistmaker.search.domain.models.TAG_CURRENT_TRACK
//import com.example.playlistmaker.search.domain.models.Track
//import org.koin.androidx.viewmodel.ext.android.viewModel
//
//class SearchActivity : AppCompatActivity() {
//    private lateinit var binding: ActivitySearchBinding
//    private val viewModel by viewModel<SearchViewModel>()
//    private var editTextSaver: String = TEXT_DEF
//    private val tracksSearch = ArrayList<Track>()
//    private val tracksHistory = mutableListOf<Track>()
//    private val handler = Handler(Looper.getMainLooper())
//    private var adapter = TrackAdapter(tracksSearch) { clickedTrack ->
//        viewModel.saveTrackToHistory(clickedTrack)
//        loadSearchHistory()
//    }
//    private var adapterHistory = TrackAdapter(tracksHistory) { clickedTrack ->
//        if (clickDebounce()) {
//            viewModel.saveTrackToHistory(clickedTrack)
//            loadSearchHistory()
//        }
//    }
//    private var isClickAllowed = true
//
//    @SuppressLint("WrongViewCast")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivitySearchBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
//            val bottomPadding = if (ime.bottom > 0) {
//                ime.bottom
//            } else {
//                systemBars.bottom
//            }
//            v.setPadding(
//                systemBars.left, systemBars.top, systemBars.right,
//                bottomPadding
//            )
//            insets
//        }
//        loadSearchHistory()
//
//        adapter = TrackAdapter(tracksSearch) { clickedTrack ->
//            if (clickDebounce()) {
//                viewModel.saveTrackToHistory(clickedTrack)
//                loadSearchHistory()
//                val displayAudioPlayerActivity = Intent(this, AudioPlayerActivity::class.java)
//                displayAudioPlayerActivity.putExtra(TAG_CURRENT_TRACK, clickedTrack)
//                startActivity(displayAudioPlayerActivity)
//            }
//        }
//
//        adapterHistory = TrackAdapter(tracksHistory) { clickedTrack ->
//            if (clickDebounce()) {
//                viewModel.saveTrackToHistory(clickedTrack)
//                loadSearchHistory()
//                val displayAudioPlayerActivity = Intent(this, AudioPlayerActivity::class.java)
//                displayAudioPlayerActivity.putExtra(TAG_CURRENT_TRACK, clickedTrack)
//                startActivity(displayAudioPlayerActivity)
//            }
//        }
//
//        binding.rvSearchResult.adapter = adapter
//        binding.rvSearchHistory.adapter = adapterHistory
//
//
//
//        viewModel.observeStateSearch().observe(this) {
//            render(it)
//        }
//
//        if (binding.searchBar.text.isEmpty()) {
//            if (tracksHistory.isNotEmpty()) {
//                showHistory()
//            } else {
//                binding.llSearchHistory.isVisible = false
//            }
//        }
//
//        binding.titleSearch.setNavigationOnClickListener {
//            finish()
//        }
//
//        val searchTextWatcher = object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                binding.llSearchHistory.isVisible = !binding.searchBar.hasFocus()
//                binding.searchClearIcon.isVisible = clearButtonVisibility(s)
//
//                viewModel.searchDebounce(
//                    changedText = s?.toString() ?: ""
//                )
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//                editTextSaver = s?.toString() ?: ""
//            }
//        }
//        searchTextWatcher.let {
//            binding.searchBar.addTextChangedListener(searchTextWatcher)
//            binding.searchBar.setOnEditorActionListener { _, actionId, _ ->
//                if (actionId == EditorInfo.IME_ACTION_DONE) {
//                    viewModel.searchDebounce(binding.searchBar.text.toString())
//                }
//                false
//            }
//            binding.searchBar.setOnFocusChangeListener { _, hasFocus ->
//                binding.llSearchHistory.isVisible = !hasFocus
//            }
//        }
//
//
//        binding.btSearchUpdate.setOnClickListener {
//            viewModel.searchDebounce(binding.searchBar.text.toString())
//            viewModel.observeStateSearch().observe(this) {
//                render(it)
//            }
//        }
//
//        binding.searchClearIcon.setOnClickListener {
//            binding.searchBar.setText("")
//            binding.searchBar.clearFocus()
//            showHistory()
//            val imm =
//                it.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
//            imm.hideSoftInputFromWindow(binding.searchBar.windowToken, 0)
//        }
//
//        binding.btClearHistory.setOnClickListener {
//            viewModel.clearHistory()
//            tracksHistory.clear()
//            adapterHistory.notifyDataSetChanged()
//            binding.llSearchHistory.isVisible = false
//        }
//    }
//
//
//    override fun onSaveInstanceState(outState: Bundle) {
//        outState.putString(EDIT_TEXT, editTextSaver)
//        super.onSaveInstanceState(outState)
//    }
//
//    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        super.onRestoreInstanceState(savedInstanceState)
//        editTextSaver = savedInstanceState.getString(EDIT_TEXT, TEXT_DEF)
//        binding.searchBar.setText(editTextSaver)
//    }
//
//    private fun clearButtonVisibility(s: CharSequence?): Boolean {
//        return !s.isNullOrEmpty()
//    }
//
//    private fun clickDebounce(): Boolean {
//        val current = isClickAllowed
//        if (isClickAllowed) {
//            isClickAllowed = false
//            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
//        }
//        return current
//    }
//
//    private fun showContent(tracks: List<Track>) {
//        tracksSearch.clear()
//        tracksSearch.addAll(tracks)
//        adapter.notifyDataSetChanged()
//        binding.llSearchHistory.isVisible = false
//        binding.rvSearchResult.isVisible = true
//        binding.searchIsEmpty.isVisible = false
//        binding.searchNoInternet.isVisible = false
//        binding.pbSearch.isVisible = false
//
//    }
//
//    private fun showEmptyResults() {
//        tracksSearch.clear()
//        adapter.notifyDataSetChanged()
//        binding.llSearchHistory.isVisible = false
//        binding.rvSearchResult.isVisible = false
//        binding.searchIsEmpty.isVisible = true
//        binding.searchNoInternet.isVisible = false
//        binding.pbSearch.isVisible = false
//    }
//
//    private fun showNetworkError() {
//        tracksSearch.clear()
//        adapter.notifyDataSetChanged()
//        binding.llSearchHistory.isVisible = false
//        binding.rvSearchResult.isVisible = false
//        binding.searchIsEmpty.isVisible = false
//        binding.searchNoInternet.isVisible = true
//        binding.pbSearch.isVisible = false
//    }
//
//    private fun showHistory() {
//        tracksSearch.clear()
//        adapter.notifyDataSetChanged()
//        loadSearchHistory()
//        binding.rvSearchResult.isVisible = false
//        binding.searchIsEmpty.isVisible = false
//        binding.searchNoInternet.isVisible = false
//        binding.pbSearch.isVisible = false
//    }
//
//    private fun showProgressBar() {
//        tracksSearch.clear()
//        adapter.notifyDataSetChanged()
//        binding.llSearchHistory.isVisible = false
//        binding.rvSearchResult.isVisible = false
//        binding.searchIsEmpty.isVisible = false
//        binding.searchNoInternet.isVisible = false
//        binding.pbSearch.isVisible = true
//    }
//
//    private fun loadSearchHistory() {
//        val saved = viewModel.loadTracksFromHistory()
//        tracksHistory.clear()
//        tracksHistory.addAll(saved)
//        adapterHistory.notifyDataSetChanged()
//        binding.llSearchHistory.isVisible = tracksHistory.isNotEmpty()
//    }
//
//    fun render(state: SearchState) {
//        when (state) {
//            is SearchState.Loading -> showProgressBar()
//            is SearchState.Content -> showContent(state.tracks)
//            is SearchState.Error -> showNetworkError()
//            is SearchState.Empty -> showEmptyResults()
//        }
//    }
//
//    companion object {
//        const val EDIT_TEXT = "EDIT_TEXT"
//        const val TEXT_DEF = ""
//        const val CLICK_DEBOUNCE_DELAY = 1000L
//    }
//}
