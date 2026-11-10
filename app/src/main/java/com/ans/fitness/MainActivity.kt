package com.ans.fitness

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ans.fitness.data.repository.FitnessRepository
import com.ans.fitness.ui.navigation.AppNavigation
import com.ans.fitness.ui.theme.AnsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = FitnessRepository(applicationContext)
        setContent {
            AnsTheme {
                AppNavigation(repository = repository)
            }
        }
    }
}
