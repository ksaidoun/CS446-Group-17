package com.example.famplanapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.famplanapp.ui.theme.FamPlanAppTheme
import androidx.compose.foundation.layout.*


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FamPlanAppTheme {
                BottomNavBar()

            }
        }
    }
}
