package com.example.caminodesantiago

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface

class MainActivity : ComponentActivity() {
    private val repo by lazy { Repository(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    MapScreen(repo = repo)
                }
            }
        }
    }
}
