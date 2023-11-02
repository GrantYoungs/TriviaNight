package com.example.trivianight.data

import com.example.trivianight.data.error.TriviaResponseException
import com.example.trivianight.data.model.network.ResponseCode
import com.example.trivianight.data.model.network.toDomain
import com.example.trivianight.util.retrofit.bodyOrError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TriviaRepository @Inject constructor(
    private val openTDBApi: OpenTDBApi
) {

    private val ioDispatcher = Dispatchers.IO

    private val _gameState = MutableStateFlow(TriviaGameState())
    val gameState = _gameState.asStateFlow()

    /**
     * Retrieve a list of Trivia Questions.
     *
     * @param numQuestions The number of questions to retrieve.
     */
    suspend fun getTriviaQuestions(numQuestions: Int) {
        withContext(ioDispatcher) {
            getOpenTDBTriviaQuestions(numQuestions = numQuestions)
        }
    }

    private suspend fun getOpenTDBTriviaQuestions(numQuestions: Int) {
        openTDBApi.getTriviaQuestions(numQuestions = numQuestions).let { response ->
            response.bodyOrError()!!.toDomain().let { questions ->

                when (ResponseCode.getResponseCode(questions.responseCode)) {
                    ResponseCode.SUCCESS -> {
                        _gameState.update { oldState ->
                            oldState.copy(
                                triviaQuestions = questions.results,
                                currentQuestionIndex = 0
                            )
                        }
                    }

                    ResponseCode.NO_RESPONSE -> {
                        throw TriviaResponseException.NoResultsException
                    }

                    ResponseCode.INVALID_PARAMETER -> {
                        throw TriviaResponseException.InvalidParameterException
                    }

                    ResponseCode.TOKEN_NOT_FOUND -> {
                        throw TriviaResponseException.TokenNotFoundException
                    }

                    ResponseCode.TOKEN_EMPTY -> {
                        throw TriviaResponseException.TokenEmptyException
                    }

                    else -> {
                        throw TriviaResponseException.UnableToRetrieveResults
                    }
                }
            }
        }
    }

    /**
     * To be called when the user selects the correct answer.
     */
    fun onCorrectAnswer() {
        _gameState.update { oldState ->
            oldState.copy(numCorrectAnswers = oldState.numCorrectAnswers + 1)
        }
    }

    /**
     * To be called when the user selects the incorrect answer.
     */
    fun onIncorrectAnswer() {
        _gameState.update { oldState ->
            oldState.copy(numIncorrectAnswers = oldState.numIncorrectAnswers + 1)
        }
    }

    /**
     * Navigates to the next question by increasing the current question index.
     */
    fun moveToNextQuestion() {
        _gameState.update { oldState ->
            oldState.copy(currentQuestionIndex = oldState.currentQuestionIndex + 1)
        }
    }
}