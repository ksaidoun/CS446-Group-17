package com.example.famplanapp.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import java.util.*

@Composable
fun CreateEvent() {
    var isDialogOpen by remember { mutableStateOf(false) }
    var eventName by remember { mutableStateOf(TextFieldValue()) }
    var attendees by remember { mutableStateOf(TextFieldValue()) }
    var selectedDateTime by remember { mutableStateOf(Date()) }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .background(Color.White)
            .border(0.5.dp, Color.Black)
            //.fillMaxSize()
            .clickable { isDialogOpen = true }
            //.padding(16.dp)
            //.background(Color.LightGray)
    ) {
        //Text("Create Event")
    }

    if (isDialogOpen) {
        AlertDialog(
            onDismissRequest = { isDialogOpen = false },
            title = { Text("Event Details") },
            confirmButton = {
                Button(onClick = { isDialogOpen = false }) {
                    Text("Save")
                }
            },
            dismissButton = {
                Button(onClick = { isDialogOpen = false }) {
                    Text("Cancel")
                }
            },
            text = {
                Column {
                    TextField(
                        eventName.text,
                        onValueChange = { eventName = TextFieldValue(it) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.body1,
                        label = { Text("Event Name") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = attendees.text,
                        onValueChange = { attendees = TextFieldValue(it) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.body1,
                        label = { Text("Attendees") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // Implement date/time selector
                }
            },
            properties = DialogProperties(dismissOnClickOutside = false)
        )
    }
}