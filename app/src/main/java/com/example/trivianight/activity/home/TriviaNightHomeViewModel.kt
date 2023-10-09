package com.example.trivianight.activity.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trivianight.data.TriviaRepository
import com.example.trivianight.data.model.domain.Question
import com.example.trivianight.activity.home.TriviaNightHomeViewModel.Action.GetTriviaQuestions
import com.example.trivianight.activity.home.TriviaNightHomeViewModel.Action.StartTriviaGame
import com.example.trivianight.util.stateflow.ViewModelFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

import javax.inject.Inject

@HiltViewModel
class TriviaNightHomeViewModel @Inject constructor(
    private val triviaRepository: TriviaRepository
) : ViewModel() {

    private val _viewStateFlow = ViewModelFlow<HomeViewState, Event>(HomeViewState(isLoading = false))

    val viewState: StateFlow<HomeViewState> get() = _viewStateFlow.viewState
    val eventFlow: SharedFlow<Event> get() = _viewStateFlow.events


    data class HomeViewState(
        val isLoading: Boolean = false,
        val homeMessage: String = "Get questions",
        val triviaQuestions: List<Question> = emptyList()
    )

    fun onAction(action: Action) {
        when (action) {
            is GetTriviaQuestions -> {
                getTriviaQuestions()
            }

            is StartTriviaGame -> {
                startTriviaGame()
            }
        }
    }

    private fun getTriviaQuestions() {
        viewModelScope.launch {
            setLoadingState(true)

            runCatching {
                triviaRepository.getTriviaQuestions(numQuestions = 5)
            }.onSuccess { questions ->
                _viewStateFlow.update { oldState ->
                    oldState.copy(
                        isLoading = false,
                        triviaQuestions = questions
                    )
                }
            }.onFailure { exception ->
                Log.e("Error", exception.message.orEmpty())
                setLoadingState(false)
            }
        }
    }

    private fun startTriviaGame() {
        _viewStateFlow.emit(Event.StartTriviaGame)
    }

    private fun setLoadingState(isLoading: Boolean) {
        _viewStateFlow.update { oldState -> oldState.copy(isLoading = isLoading) }
    }

    sealed class Action {
        object GetTriviaQuestions : Action()
        object StartTriviaGame : Action()
    }

    sealed class Event {
        object StartTriviaGame : Event()
    }
}