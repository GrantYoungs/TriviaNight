package com.example.trivianight.activity.home

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.example.trivianight.R
import com.example.trivianight.data.model.domain.Question
import com.example.trivianight.activity.home.TriviaNightHomeViewModel.Action.StartTriviaGame
import com.example.trivianight.util.stateflow.ViewModelFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class TriviaNightHomeViewModel @Inject constructor() : ViewModel() {

    private val _viewStateFlow = ViewModelFlow<HomeViewState, Event>(HomeViewState(isLoading = false))

    val viewState: StateFlow<HomeViewState> get() = _viewStateFlow.viewState
    val eventFlow: SharedFlow<Event> get() = _viewStateFlow.events


    data class HomeViewState(
        val isLoading: Boolean = false,
        @StringRes val homeMessage: Int = R.string.welcome_to_trivia_night,
    )

    fun onAction(action: Action) {
        when (action) {
            is StartTriviaGame -> {
                startTriviaGame()
            }
        }
    }

    private fun startTriviaGame() {
        _viewStateFlow.emit(Event.StartTriviaGame)
    }

    sealed class Action {
        object StartTriviaGame : Action()
    }

    sealed class Event {
        object StartTriviaGame : Event()
    }
}