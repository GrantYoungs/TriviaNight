package com.example.trivianight.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trivianight.data.TriviaRepository
import com.example.trivianight.ui.TriviaNightHomeViewModel.TriviaNightHomeAction.GetTriviaQuestions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import javax.inject.Inject

@HiltViewModel
class TriviaNightHomeViewModel @Inject constructor(
    private val triviaRepository: TriviaRepository
) : ViewModel() {

    private val _viewState: MutableStateFlow<HomeViewState> = MutableStateFlow(HomeViewState(isLoading = false))
    val viewState: StateFlow<HomeViewState> get() = _viewState.asStateFlow()

    data class HomeViewState(
        val isLoading: Boolean = false,
        val homeMessage: String = "Welcome to Trivia Night!"
    )

    fun onAction(action: TriviaNightHomeAction) {
        when (action) {
            is GetTriviaQuestions -> {
                getTriviaQuestions()
            }
        }
    }

    private fun getTriviaQuestions() {
        viewModelScope.launch {
            setLoadingState(true)

            runCatching {
                val questions = triviaRepository.getTriviaQuestions(numQuestions = 5)

                _viewState.update { oldState ->
                    oldState.copy(
                        isLoading = false,
                        homeMessage = "${questions.results.size} questions ready!"
                    )
                }
            }.onFailure {
                Log.e("fuck", it.message.orEmpty())
                setLoadingState(false)
            }
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        _viewState.update { oldState -> oldState.copy(isLoading = isLoading) }
    }

    sealed class TriviaNightHomeAction {
        object GetTriviaQuestions: TriviaNightHomeAction()
    }
}