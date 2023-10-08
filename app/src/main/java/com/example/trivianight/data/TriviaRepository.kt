package com.example.trivianight.data

import com.example.trivianight.data.error.TriviaResponseException
import com.example.trivianight.data.model.domain.Question
import com.example.trivianight.data.model.network.toDomain
import com.example.trivianight.util.retrofit.bodyOrError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

private const val SUCCESS_CODE = 0

@Singleton
class TriviaRepository @Inject constructor(
    private val triviaApi: TriviaApi
) {

    private val ioDispatcher = Dispatchers.IO

    suspend fun getTriviaQuestions(numQuestions: Int = 5): List<Question> {
        return withContext(ioDispatcher) {
            triviaApi.getTriviaQuestions(numQuestions = numQuestions).let { response ->
                response.bodyOrError()!!.toDomain().let { questions ->
                    if (questions.responseCode == SUCCESS_CODE) {
                        return@withContext questions.results
                    } else {
                        throw TriviaResponseException.UnableToRetrieveQuestions
                    }
                }
            }
        }
    }
}