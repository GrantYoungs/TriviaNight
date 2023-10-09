package com.example.trivianight.activity.game

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trivianight.data.TriviaRepository
import com.example.trivianight.data.model.domain.Question
import com.example.trivianight.util.stateflow.ViewModelFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// The default number of questions to retrieve.
// TODO: Take user input for number of questions
private const val NUM_QUESTIONS = 2

@HiltViewModel
class TriviaNightGameViewModel @Inject constructor(
    private val triviaRepository: TriviaRepository
) : ViewModel() {

    private val _viewStateFlow = ViewModelFlow<GameViewState, Event>(GameViewState(isLoading = false))

    val viewState: StateFlow<GameViewState> get() = _viewStateFlow.viewState
    val eventFlow: SharedFlow<Event> get() = _viewStateFlow.events

    init {
        getTriviaQuestions(numQuestions = NUM_QUESTIONS)
    }

    data class GameViewState(
        val isLoading: Boolean = false,
        private val triviaQuestions: List<Question> = emptyList(),
        private val currentQuestionIndex: Int = 0,
        val message: String = "Getting questions"
    ) {
        val currentQuestion: Question?
            get() = triviaQuestions.getOrNull(currentQuestionIndex)
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.DisplayNextQuestion -> {
                
            }
        }
    }

    private fun getTriviaQuestions(numQuestions: Int) {
        viewModelScope.launch {
            setLoadingState(true)

            runCatching {
                triviaRepository.getTriviaQuestions(numQuestions = numQuestions)
            }.onSuccess { questions ->
                _viewStateFlow.update { oldState ->
                    oldState.copy(
                        isLoading = false,
                        triviaQuestions = questions,
                        message = "${questions.size} questions ready!"
                    )
                }
            }.onFailure { exception ->
                Log.e("Error", exception.message.orEmpty())
                setLoadingState(false)
            }
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        _viewStateFlow.update { oldState -> oldState.copy(isLoading = isLoading) }
    }

    sealed class Action {
        object DisplayNextQuestion : Action()
    }

    sealed class Event {

    }
}