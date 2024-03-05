package com.example.famplanapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.time.LocalDateTime
import java.util.Calendar

class TasksCreation(navController: NavController) {
}


var taskIdCount = 1


@Composable
fun ReadonlyOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    label: @Composable () -> Unit
) {
    Box {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier.fillMaxWidth(),
            label = label,

            )
        Box(
            modifier = Modifier
                .matchParentSize()
                .alpha(0f)
                .clickable(onClick = onClick),
        )
    }
}

@Composable
fun TasksDatePicker() {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // getting today's date fields
    val year = calendar[Calendar.YEAR]
    val month = calendar[Calendar.MONTH]
    val day = calendar[Calendar.DAY_OF_MONTH]

    var selectedDateText by remember { mutableStateOf("") }

    val datePicker =
        DatePickerDialog(
            context,
            { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                selectedDateText = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            },
            year,
            month,
            day,
        )
    // can't pick dates in the past
    datePicker.datePicker.minDate = calendar.timeInMillis

    Column(
        horizontalAlignment = Alignment.Start
    ) {
        ReadonlyOutlinedTextField(
            value = selectedDateText,
            onValueChange = { selectedDateText = it },
            onClick = {
                datePicker.show()
            }
        ) {
            Text(text = "Due Date")
        }
    }
}

@Composable
fun TasksTimePicker() {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    var selectedTimeText by remember { mutableStateOf("") }
    // get current hour and minute
    val hour = calendar[Calendar.HOUR_OF_DAY]
    val minute = calendar[Calendar.MINUTE]
    val timePicker = TimePickerDialog(
        context,
        { _, selectedHour: Int, selectedMinute: Int ->
            selectedTimeText = "$selectedHour:$selectedMinute"
        }, hour, minute, false
    )
    Column(
        horizontalAlignment = Alignment.Start
    ) {
        ReadonlyOutlinedTextField(
            value = selectedTimeText,
            onValueChange = { selectedTimeText = it },
            onClick = {
                timePicker.show()
            }
        ) {
            Text(text = "Reminder Time")
        }
    }
}

@Composable
fun FilterDropdown() {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("My Tasks", "All Tasks", "Subscribed Tasks", "Unassigned Tasks")
    var selectedItem by remember { mutableStateOf("My Tasks") }
    //var mTextFieldSize by remember { mutableStateOf(Size.Zero)}

    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    Column(Modifier.padding(20.dp)) {
        OutlinedTextField(
            value = selectedItem,
            onValueChange = { selectedItem = it },
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .onGloballyPositioned { coordinates ->
                },
            trailingIcon = {
                Icon(icon,"contentDescription",
                    Modifier.clickable { expanded = !expanded })
            },
            readOnly = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.None)
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth(0.5f)
        ) {
            options.forEach { label ->
                DropdownMenuItem(onClick = {
                    selectedItem = label
                    expanded = false
                }) {
                    Text(text = label)
                }
            }
        }
    }
}

@Composable
fun TaskCreator(addTask: (Task) -> Unit) {
    val id = taskIdCount
    var title by remember { mutableStateOf("") }
    var dueDate = LocalDateTime.now()
    var notes by remember { mutableStateOf("") }
    taskIdCount++
    Column(
        modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        TasksDatePicker()
        Spacer(modifier = Modifier.height(16.dp))
        TasksTimePicker()
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val task = Task(id = id, title = title, dueDate = dueDate, notes = notes)
                addTask(task)
                title = ""
                dueDate = LocalDateTime.now()
                notes = ""
                // later call a createTask function in model/viewmodel?
            },

            modifier = Modifier.align(Alignment.End),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.White,
                contentColor = Color(darkPurple)
            )
        ) {
            Text(
                "Create Task",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            )
        }
    }
}
