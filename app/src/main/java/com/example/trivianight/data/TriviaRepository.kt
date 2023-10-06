package com.example.trivianight.data

import com.example.trivianight.data.model.domain.TriviaQuestions
import com.example.trivianight.data.model.network.toDomain
import com.example.trivianight.util.retrofit.bodyOrError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TriviaRepository @Inject constructor(
    private val triviaApi: TriviaApi
) {

    private val ioDispatcher = Dispatchers.IO

    suspend fun getTriviaQuestions(numQuestions: Int): TriviaQuestions {
        return withContext(ioDispatcher) {
            triviaApi.getTriviaQuestions().let { response ->
                response.bodyOrError()!!.toDomain()
            }
        }
    }
}