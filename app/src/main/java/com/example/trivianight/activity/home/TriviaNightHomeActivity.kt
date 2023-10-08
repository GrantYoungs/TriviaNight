package com.example.trivianight.activity.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.trivianight.R
import com.example.trivianight.activity.game.TriviaNightGameActivity
import com.example.trivianight.ui.theme.TriviaNightTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class TriviaNightHomeActivity : ComponentActivity() {
    private val viewModel: TriviaNightHomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TriviaNightTheme(dynamicColor = false) {
                val viewState by remember { viewModel.viewStateFlow }.collectAsState()

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ConstraintLayout(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val (title, getStartedButton) = createRefs()

                        Text(
                            // text = stringResource(R.string.welcome_to_trivia_night),
                            text = viewState.homeMessage,
                            modifier = Modifier.constrainAs(title) {
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                centerVerticallyTo(parent)
                            },
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleLarge
                        )

                        Button(
                            onClick = {
                                viewModel.onAction(TriviaNightHomeViewModel.Action.StartTriviaGame)
                            },
                            modifier = Modifier
                                .size(width = 300.dp, height = 50.dp)
                                .constrainAs(getStartedButton) {
                                    top.linkTo(title.bottom, 20.dp)
                                    centerHorizontallyTo(parent)
                                },
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 5.dp
                            )
                        ) {
                            Text(
                                text = stringResource(R.string.lets_play)
                            )
                        }
                    }
                }

                LaunchedEffect(true) {
                    viewModel.eventFlow.onEach { event ->
                        when (event) {
                            is TriviaNightHomeViewModel.Event.StartTriviaGame -> {
                                startTriviaGame()
                            }
                        }
                    }.launchIn(this)
                }
            }
        }
    }

    private fun startTriviaGame() {
        startActivity(
            TriviaNightGameActivity.newIntent(this)
        )
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TriviaNightTheme {
        Greeting("Android")
    }
}