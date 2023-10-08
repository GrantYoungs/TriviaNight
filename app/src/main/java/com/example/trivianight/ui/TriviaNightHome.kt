package com.example.trivianight.ui

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.trivianight.R
import com.example.trivianight.ui.theme.TriviaNightTheme
import com.example.trivianight.ui.TriviaNightHomeViewModel.TriviaNightHomeAction as Action
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TriviaNightHome : ComponentActivity() {
    private val triviaNightHomeViewModel: TriviaNightHomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TriviaNightTheme(dynamicColor = false) {
                val viewState by triviaNightHomeViewModel.viewState.collectAsState()

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
                            text = stringResource(R.string.welcome_to_trivia_night),
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
            }
        }
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