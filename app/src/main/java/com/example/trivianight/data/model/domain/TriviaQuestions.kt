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
    val correctAnswer: Answer,
    val incorrectAnswers: List<Answer>,
    val allAnswers: List<Answer>
)

data class Answer(
    val value: String,
    val isCorrect: Boolean
)