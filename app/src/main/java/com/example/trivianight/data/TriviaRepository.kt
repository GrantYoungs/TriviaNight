package com.example.trivianight.data

import com.example.trivianight.data.api.OpenTDBApi
import com.example.trivianight.data.api.TheTriviaApi
import com.example.trivianight.data.error.TriviaResponseException
import com.example.trivianight.data.model.domain.Question
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
    private val openTDBApi: OpenTDBApi,
    private val theTriviaApi: TheTriviaApi
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
            // getOpenTDBTriviaQuestions(numQuestions = numQuestions)
            // getTheTriviaApiQuestions()

            val theTriviaApiQuestions = getTheTriviaApiQuestions()
            val openTDBquestions = getOpenTDBTriviaQuestions(numQuestions = numQuestions)

            theTriviaApiQuestions.plus(openTDBquestions).let { questions ->
                _gameState.update { oldState ->
                    oldState.copy(
                        triviaQuestions = questions,
                        currentQuestionIndex = 0
                    )
                }
            }
        }
    }

    // TODO When one source fails, the user shouldn't be blocked unless every source fails
    private suspend fun getOpenTDBTriviaQuestions(numQuestions: Int): List<Question> {
        openTDBApi.getTriviaQuestions(numQuestions = numQuestions).let { response ->
            response.bodyOrError()!!.toDomain().let { questions ->

                return when (ResponseCode.getResponseCode(questions.responseCode)) {
                    ResponseCode.SUCCESS -> {
                       questions.results
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

    private suspend fun getTheTriviaApiQuestions(): List<Question> {
        return theTriviaApi.getTriviaQuestions().let { response ->
            response.bodyOrError()!!.let { questionsResponse ->
                questionsResponse.map { it.toDomain() }
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