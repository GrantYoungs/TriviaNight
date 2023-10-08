package com.example.trivianight.data.error

sealed class TriviaResponseException(
    private val errorMessage: String
) : Exception(errorMessage) {

    object UnableToRetrieveQuestions : TriviaResponseException("Unable to retrieve questions")
}