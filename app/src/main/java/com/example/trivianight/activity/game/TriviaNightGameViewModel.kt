package com.example.trivianight.activity.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trivianight.data.TriviaGameState
import com.example.trivianight.data.TriviaRepository
import com.example.trivianight.ui.decorators.QuestionDecorator
import com.example.trivianight.util.stateflow.ViewModelFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// The default number of questions to retrieve.
// TODO: Take user input for number of questions
private const val NUM_QUESTIONS = 5

@HiltViewModel
class TriviaNightGameViewModel @Inject constructor(
    private val triviaRepository: TriviaRepository
) : ViewModel() {

    private val _viewStateFlow = ViewModelFlow<GameViewState, Event>(GameViewState(isLoading = false))

    val viewState: StateFlow<GameViewState> get() = _viewStateFlow.viewState
    val eventFlow: SharedFlow<Event> get() = _viewStateFlow.events

    init {
        viewModelScope.launch {
            triviaRepository.gameState.collect { gameState ->
                onNewGameState(gameState)
            }
        }

        getTriviaQuestions(numQuestions = NUM_QUESTIONS)
    }

    private fun onNewGameState(gameState: TriviaGameState) {
        _viewStateFlow.update { oldState ->
            oldState.copy(
                correctAnswerCounter = gameState.numCorrectAnswers.toString(),
                incorrectAnswerCounter = gameState.numIncorrectAnswers.toString(),
                currentQuestion = gameState.currentQuestion?.let { question ->
                    QuestionDecorator.decorate(question = question)
                }
            )
        }
    }

    data class GameViewState(
        val isLoading: Boolean = false,
        val currentQuestion: QuestionDecorator? = null,
        val userHasGuessed: Boolean = false,
        val displayErrorDialog: Boolean = false,
        val correctAnswerCounter: String = "",
        val incorrectAnswerCounter: String = "",
    )

    fun onAction(action: Action) {
        when (action) {
            is Action.GetTriviaQuestions -> {
                getTriviaQuestions(numQuestions = NUM_QUESTIONS)
            }

            is Action.DisplayNextQuestion -> {
                displayNextQuestion()
            }

            is Action.CheckAnswer -> {
                checkAnswer(action.answer)
            }

            is Action.CloseErrorDialog -> {
                closeErrorDialog()
            }
        }
    }

    private fun getTriviaQuestions(numQuestions: Int) {
        viewModelScope.launch {
            setLoadingState(true)

            val isSuccessful = triviaRepository.getTriviaQuestions(numQuestions = numQuestions)

            setLoadingState(false)

            if (!isSuccessful) {
                _viewStateFlow.update { oldState ->
                    oldState.copy(
                        displayErrorDialog = true
                    )
                }
            }
        }
    }

    private fun checkAnswer(answer: String) {
        _viewStateFlow.update { oldState ->
            oldState.copy(userHasGuessed = true)
        }

        if (answer == _viewStateFlow.viewState.value.currentQuestion?.correctAnswer?.value) {
            triviaRepository.onCorrectAnswer()
        } else {
            triviaRepository.onIncorrectAnswer()
        }
    }

    private fun displayNextQuestion() {
        triviaRepository.moveToNextQuestion()

        _viewStateFlow.update { oldState ->
            oldState.copy(
                userHasGuessed = false
            )
        }

        if (_viewStateFlow.viewState.value.currentQuestion == null) {
            getTriviaQuestions(numQuestions = NUM_QUESTIONS)
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        _viewStateFlow.update { oldState -> oldState.copy(isLoading = isLoading) }
    }

    private fun closeErrorDialog() {
        _viewStateFlow.update { oldState -> oldState.copy(displayErrorDialog = false) }
    }

    sealed class Action {
        object GetTriviaQuestions : Action()

        object DisplayNextQuestion : Action()

        data class CheckAnswer(val answer: String) : Action()

        object CloseErrorDialog : Action()
    }

    sealed class Event {

    }
}