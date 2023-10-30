package com.example.trivianight.activity.game

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.trivianight.R
import com.example.trivianight.ui.theme.TriviaNightTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TriviaNightGameActivity : ComponentActivity() {

    private val viewModel: TriviaNightGameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TriviaNightTheme(dynamicColor = false) {
                val viewState by viewModel.viewState.collectAsState()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ConstraintLayout(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val (questionTitle, loadingIndicator, nextQuestionButton, answersList) = createRefs()

                        if (viewState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(40.dp)
                                    .constrainAs(loadingIndicator) {
                                        centerHorizontallyTo(parent)
                                        centerVerticallyTo(parent)
                                    }
                            )
                        }

                        viewState.currentQuestion?.let { question ->
                            Text(
                                text = question.question,
                                modifier = Modifier
                                    .fillMaxWidth(0.80f)
                                    .constrainAs(questionTitle) {
                                        top.linkTo(parent.top, 80.dp)
                                        centerHorizontallyTo(parent)
                                    },
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.headlineMedium
                            )

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth(0.65f)
                                    .constrainAs(answersList) {
                                        centerHorizontallyTo(parent)
                                        centerVerticallyTo(parent)
                                    }
                            ) {
                                question.allAnswers.forEach { answer ->
                                    val buttonColor = if (viewState.userHasGuessed && answer.isCorrect) Color.Green else MaterialTheme.colorScheme.primary
                                    val disabledColor = buttonColor.copy(alpha = 0.38f)

                                    Button(
                                        onClick = {
                                            viewModel.onAction(
                                                TriviaNightGameViewModel.Action.CheckAnswer(answer = answer.value)
                                            )
                                        },
                                        enabled = viewState.userHasGuessed.not(),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 10.dp),
                                        elevation = ButtonDefaults.buttonElevation(
                                            defaultElevation = 3.dp
                                        ),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = buttonColor,
                                            disabledContainerColor = disabledColor
                                        )
                                    ) {
                                        Text(
                                            text = answer.value,
                                            textAlign = TextAlign.Center,
                                            style = MaterialTheme.typography.titleLarge
                                        )
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = {
                                viewModel.onAction(TriviaNightGameViewModel.Action.DisplayNextQuestion)
                            },
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .height(50.dp)
                                .constrainAs(nextQuestionButton) {
                                    centerHorizontallyTo(parent)
                                    bottom.linkTo(parent.bottom, 40.dp)
                                },
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 5.dp
                            )
                        ) {
                            Text(
                                text = stringResource(R.string.next_question),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }

                    if (viewState.displayErrorDialog) {
                        ErrorDialog(
                            title = stringResource(R.string.unable_to_retrieve_questions_title),
                            message = stringResource(R.string.unable_to_retrieve_questions_message),
                            onConfirmation = {
                                viewModel.onAction(TriviaNightGameViewModel.Action.CloseErrorDialog)
                                viewModel.onAction(TriviaNightGameViewModel.Action.GetTriviaQuestions)
                            },
                            onDismiss = {
                                viewModel.onAction(TriviaNightGameViewModel.Action.CloseErrorDialog)
                                finish()
                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun ErrorDialog(
        title: String,
        message: String,
        onConfirmation: () -> Unit,
        onDismiss: () -> Unit
    ) {
        AlertDialog(
            title = {
                Text(text = title)
            },
            text = {
                Text(text = message)
            },
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = onConfirmation
                ) {
                    Text(text = stringResource(R.string.retry_button))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text(text = stringResource(R.string.ok))
                }
            }
        )
    }

    companion object {
        fun newIntent(
            context: Context
        ): Intent = Intent(context, TriviaNightGameActivity::class.java)
    }
}