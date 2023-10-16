package com.example.trivianight.data.error

sealed class TriviaResponseException(
    private val errorMessage: String
) : Exception(errorMessage) {

    object NoResultsException : TriviaResponseException("Could not return results. The API doesn't have enough questions for your query.")

    object InvalidParameterException : TriviaResponseException("Contains an invalid parameter. Arguments passed in aren't valid.")

    object TokenNotFoundException : TriviaResponseException("Session Token does not exist.")

    object TokenEmptyException : TriviaResponseException("Session Token has returned all possible questions for the specified query. Resetting the Token is necessary.")

    object UnableToRetrieveResults : TriviaResponseException("Unable to retrieve trivia questions.")
}