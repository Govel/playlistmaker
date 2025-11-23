package com.example.playlistmaker.main.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.util.SingleLiveEvent

class MainViewModel: ViewModel() {
    private val dispatchLiveData = SingleLiveEvent<MainState>()
    fun observeDispatcher(): LiveData<MainState> = dispatchLiveData

    private fun setState(state: MainState) {
        dispatchLiveData.value = state
    }

    fun dispatchSearch() {
        setState(MainState.DispatchSearch)
    }

    fun dispatchMedia() {
        setState(MainState.DispatchMedia)
    }

    fun dispatchSettings() {
        setState(MainState.DispatchSettings)
    }
}