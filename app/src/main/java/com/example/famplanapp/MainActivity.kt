package com.example.famplanapp

import SignInScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.famplanapp.ui.theme.FamPlanAppTheme


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
