package com.example.playlistmaker.search.ui


import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.util.debounce
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var onTrackClickDebounce: (Track) -> Unit
    private val viewModel by viewModel<SearchViewModel>()
    private var editTextSaver: String = TEXT_DEF
    private val tracksSearch = ArrayList<Track>()
    private val tracksHistory = mutableListOf<Track>()

    private var adapter = TrackAdapter(tracksSearch) { clickedTrack ->
        if (clickDebounce()) {
            viewModel.saveTrackToHistory(clickedTrack)
            loadSearchHistory()
        }
    }
    private var adapterHistory = TrackAdapter(tracksHistory) { clickedTrack ->
        if (clickDebounce()) {
            viewModel.saveTrackToHistory(clickedTrack)
            loadSearchHistory()
        }
    }
    private var isClickAllowed = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        renderHistoryIfNeeded()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { clickedTrack ->
            viewModel.saveTrackToHistory(clickedTrack)
            loadSearchHistory()
        }

        savedInstanceState?.let {
            val savedText = it.getString(EDIT_TEXT, TEXT_DEF)
            binding.searchBar.setText(savedText)
        }

        loadSearchHistory()

        adapter = TrackAdapter(tracksSearch) { clickedTrack ->
            if (clickDebounce()) {
                viewModel.saveTrackToHistory(clickedTrack)
                loadSearchHistory()
                val action =
                    SearchFragmentDirections.actionSearchFragmentToAudioPlayerFragment(clickedTrack)
                findNavController().navigate(action)
            }
        }

        adapterHistory = TrackAdapter(tracksHistory) { clickedTrack ->
            if (clickDebounce()) {
                viewModel.saveTrackToHistory(clickedTrack)
                loadSearchHistory()
                val action =
                    SearchFragmentDirections.actionSearchFragmentToAudioPlayerFragment(clickedTrack)
                findNavController().navigate(action)
            }
        }
        binding.rvSearchResult.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvSearchResult.adapter = adapter
        binding.rvSearchHistory.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvSearchHistory.adapter = adapterHistory

        viewModel.observeStateSearch().observe(viewLifecycleOwner) {
            render(it)
        }

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    renderHistoryIfNeeded()
                } else {
                    binding.llSearchHistory.isVisible = false
                }
                binding.searchClearIcon.isVisible = clearButtonVisibility(s)

                viewModel.searchDebounce(
                    changedText = s?.toString() ?: ""
                )
            }

            override fun afterTextChanged(s: Editable?) {
                editTextSaver = s?.toString() ?: ""
            }
        }
        searchTextWatcher.let {
            binding.searchBar.addTextChangedListener(searchTextWatcher)
            binding.searchBar.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    viewModel.searchDebounce(binding.searchBar.text.toString())
                }
                false
            }
            binding.searchBar.setOnFocusChangeListener { _, hasFocus ->
                binding.llSearchHistory.isVisible = !hasFocus
            }
        }

        binding.btSearchUpdate.setOnClickListener {
            viewModel.searchDebounce(binding.searchBar.text.toString())
        }

        binding.searchClearIcon.setOnClickListener {
            binding.searchBar.setText("")
            binding.searchBar.clearFocus()
            showHistory()
            val imm =
                it.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.searchBar.windowToken, 0)
        }

        binding.btClearHistory.setOnClickListener {
            viewModel.clearHistory()
            tracksHistory.clear()
            adapterHistory.notifyDataSetChanged()
            binding.llSearchHistory.isVisible = false
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(EDIT_TEXT, editTextSaver)
        super.onSaveInstanceState(outState)
    }

//    override fun onViewStateRestored(savedInstanceState: Bundle?) {
//        super.onViewStateRestored(savedInstanceState)
//        editTextSaver = savedInstanceState?.getString(EDIT_TEXT, TEXT_DEF).toString()
//        binding.searchBar.setText(editTextSaver)
//    }


    private fun clearButtonVisibility(s: CharSequence?): Boolean {
        return !s.isNullOrEmpty()
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    private fun showContent(tracks: List<Track>) {
        tracksSearch.clear()
        tracksSearch.addAll(tracks)
        adapter.notifyDataSetChanged()
        binding.llSearchHistory.isVisible = false
        binding.rvSearchResult.isVisible = true
        binding.searchIsEmpty.isVisible = false
        binding.searchNoInternet.isVisible = false
        binding.pbSearch.isVisible = false
    }

    private fun showEmptyResults() {
        tracksSearch.clear()
        adapter.notifyDataSetChanged()
        binding.llSearchHistory.isVisible = false
        binding.rvSearchResult.isVisible = false
        binding.searchIsEmpty.isVisible = true
        binding.searchNoInternet.isVisible = false
        binding.pbSearch.isVisible = false
    }

    private fun showNetworkError() {
        tracksSearch.clear()
        adapter.notifyDataSetChanged()
        binding.llSearchHistory.isVisible = false
        binding.rvSearchResult.isVisible = false
        binding.searchIsEmpty.isVisible = false
        binding.searchNoInternet.isVisible = true
        binding.pbSearch.isVisible = false
    }

    private fun showHistory() {
        loadSearchHistory()
        binding.rvSearchResult.isVisible = false
        binding.searchIsEmpty.isVisible = false
        binding.searchNoInternet.isVisible = false
        binding.pbSearch.isVisible = false
    }

    private fun showProgressBar() {
        tracksSearch.clear()
        adapter.notifyDataSetChanged()
        binding.llSearchHistory.isVisible = false
        binding.rvSearchResult.isVisible = false
        binding.searchIsEmpty.isVisible = false
        binding.searchNoInternet.isVisible = false
        binding.pbSearch.isVisible = true
    }

    private fun loadSearchHistory() {
        val saved = viewModel.loadTracksFromHistory()
        tracksHistory.clear()
        tracksHistory.addAll(saved)
        adapterHistory.notifyDataSetChanged()
        binding.llSearchHistory.isVisible = tracksHistory.isNotEmpty()
    }

    private fun showStandBy() {
        binding.rvSearchResult.isVisible = false
        binding.searchIsEmpty.isVisible = false
        binding.searchNoInternet.isVisible = false
        binding.pbSearch.isVisible = false
        renderHistoryIfNeeded()
    }

    fun render(state: SearchState) {
        val query = binding.searchBar.text?.toString().orEmpty()
        if (query.isEmpty()) {
            showStandBy()
            return
        }
        when (state) {
            is SearchState.StandBy -> showStandBy()
            is SearchState.Loading -> showProgressBar()
            is SearchState.Content -> showContent(state.tracks)
            is SearchState.Error -> showNetworkError()
            is SearchState.Empty -> showEmptyResults()
        }
    }

    private fun renderHistoryIfNeeded() {
        val query = binding.searchBar.text?.toString().orEmpty()
        if (query.isEmpty()) {
            loadSearchHistory()
            binding.llSearchHistory.isVisible = tracksHistory.isNotEmpty()
        } else {
            binding.llSearchHistory.isVisible = false
        }
    }

    companion object {
        const val EDIT_TEXT = "EDIT_TEXT"
        const val TEXT_DEF = ""
        const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}