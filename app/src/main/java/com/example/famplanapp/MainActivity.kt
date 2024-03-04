package com.example.famplanapp

import SignInScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.famplanapp.ui.theme.FamPlanAppTheme


// Define anything to be used across the app here maybe?
val darkPurple = 0xFF220059
val lightPurple = 0xFFEDE7F7
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FamPlanAppTheme {
                SignInScreen()
            }
        }
    }
}
