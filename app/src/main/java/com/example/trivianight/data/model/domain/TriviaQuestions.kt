package com.example.trivianight.data.model.domain

data class TriviaQuestions(
    val responseCode: Int,
    val results: List<Question>
)

data class Question(
    val category: String,
    val type: String,
    val difficulty: String,
    val question: String,
    val correctAnswer: String,
    val incorrectAnswers: List<String>
)