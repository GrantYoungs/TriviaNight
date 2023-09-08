package com.example.trivianight.ui

import androidx.lifecycle.ViewModel
import com.example.trivianight.data.TriviaApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TriviaNightHomeViewModel @Inject constructor(
    private val triviaApi: TriviaApi
) : ViewModel() {

    fun test(): String {
        return "This is a test"
    }

}