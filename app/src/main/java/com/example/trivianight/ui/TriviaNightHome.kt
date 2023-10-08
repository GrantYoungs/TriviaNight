package com.example.trivianight.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.trivianight.ui.theme.TriviaNightTheme
import com.example.trivianight.ui.TriviaNightHomeViewModel.TriviaNightHomeAction as Action
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TriviaNightHome : ComponentActivity() {
    private val triviaNightHomeViewModel: TriviaNightHomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TriviaNightTheme {
                val viewState by triviaNightHomeViewModel.viewState.collectAsState()

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = {
                                triviaNightHomeViewModel.onAction(
                                    Action.GetTriviaQuestions
                                )
                            }
                        ) {
                            Text(
                                text = viewState.homeMessage
                            )
                        }

                        if (viewState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    LazyColumn(
                        state = rememberLazyListState(),
                        modifier = Modifier
                            .padding(top = 50.dp)
                            .fillMaxWidth(0.85f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(
                            items = viewState.triviaQuestions,
                            itemContent = { question ->
                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 5.dp),
                                    text = question.question
                                )
                            }
                        )
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