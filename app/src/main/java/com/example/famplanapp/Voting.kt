package com.example.famplanapp

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Voting(innerPadding: PaddingValues){
    Text("Voting", Modifier.padding(innerPadding))
}