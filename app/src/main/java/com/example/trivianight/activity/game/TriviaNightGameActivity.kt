package com.example.trivianight.activity.game

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import com.example.trivianight.ui.theme.TriviaNightTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TriviaNightGameActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TriviaNightTheme(dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Text(
                        text = "You're on the Game Activity",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }
    }

    companion object {
        fun newIntent(
            context: Context
        ): Intent = Intent(context, TriviaNightGameActivity::class.java)
    }
}