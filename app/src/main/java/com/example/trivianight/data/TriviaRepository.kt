package com.example.trivianight.data

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
    private val triviaApi: TriviaApi
) {

    private val ioDispatcher = Dispatchers.IO

    private val _gameState = MutableStateFlow(TriviaGameState())
    val gameState = _gameState.asStateFlow()

    suspend fun getTriviaQuestions(numQuestions: Int = 5): List<Question> {
        return withContext(ioDispatcher) {
            triviaApi.getTriviaQuestions(numQuestions = numQuestions).let { response ->
                response.bodyOrError()!!.toDomain().let { questions ->

                    when (ResponseCode.getResponseCode(questions.responseCode)) {
                        ResponseCode.SUCCESS -> {
                            _gameState.update { oldState ->
                                oldState.copy(
                                    triviaQuestions = questions.results
                                )
                            }
                            return@withContext questions.results
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
    }

    fun onCorrectAnswer() {
        _gameState.update { oldState ->
            oldState.copy(numCorrectAnswers = oldState.numCorrectAnswers + 1)
        }
    }
}