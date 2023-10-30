package com.example.trivianight.data

import com.example.trivianight.data.model.domain.Question

data class TriviaGameState(
    val numCorrectAnswers: Int = 0,
    val numIncorrectAnswers: Int = 0,
    val triviaQuestions: List<Question> = emptyList(),
    val currentQuestionIndex: Int = 0
) {
    val currentQuestion: Question?
        get() = triviaQuestions.getOrNull(currentQuestionIndex)
}