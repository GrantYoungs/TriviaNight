package com.example.trivianight.data

import android.util.Log
import com.example.trivianight.data.api.OpenTDBApi
import com.example.trivianight.data.api.TheTriviaApi
import com.example.trivianight.data.error.TriviaResponseException
import com.example.trivianight.data.model.domain.Question
import com.example.trivianight.data.model.network.ResponseCode
import com.example.trivianight.data.model.network.toDomain
import com.example.trivianight.util.retrofit.bodyOrError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.coroutineContext

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
     * @return true if questions have been retrieved successfully.
     */
    suspend fun getTriviaQuestions(numQuestions: Int): Boolean {
        return withContext(ioDispatcher) {
            val triviaQuestions: MutableList<Question> = mutableListOf()

            getTheTriviaApiQuestions()?.let { triviaQuestions.addAll(it) }
            getOpenTDBTriviaQuestions(numQuestions = numQuestions)?.let { triviaQuestions.addAll(it) }

            triviaQuestions.isNotEmpty().also {
                _gameState.update { oldState ->
                    oldState.copy(
                        triviaQuestions = triviaQuestions.shuffled(),
                        currentQuestionIndex = 0
                    )
                }
            }
        }
    }

    /**
     * Retrieve trivia questions from OpenTDB.
     *
     * @return [List]<[Question]> if successful.
     */
    private suspend fun getOpenTDBTriviaQuestions(numQuestions: Int): List<Question>? {
        return runCatching {
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
        }.getOrElse { exception ->
            coroutineContext.ensureActive()
            Log.e(LOG_TAG, exception.toString())
            if (exception is Error) throw exception

            null
        }
    }

    /**
     * Retrieve trivia questions from TheTriviaApi.
     *
     * @return [List]<[Question]> if successful.
     */
    private suspend fun getTheTriviaApiQuestions(): List<Question>? {
        return runCatching {
            theTriviaApi.getTriviaQuestions().bodyOrError()!!.map { it.toDomain() }
        }.getOrElse { exception ->
            coroutineContext.ensureActive()
            Log.e(LOG_TAG, exception.toString())
            if (exception is Error) throw exception

            null
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

    companion object {
        private const val LOG_TAG = "TriviaRepository"
    }
}