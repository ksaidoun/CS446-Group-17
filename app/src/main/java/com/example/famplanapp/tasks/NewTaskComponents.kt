package com.example.famplanapp.tasks

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.example.famplanapp.darkPurple
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Locale

var taskIdCount = 0
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
            modifier = modifier.width(120.dp),
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
fun TasksDatePicker(defaultText: String, defaultDate: LocalDateTime?): LocalDateTime? {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }
    var selectedDateText by remember { mutableStateOf("") }

    // getting today's date fields
    var year = calendar[Calendar.YEAR]
    var month = calendar[Calendar.MONTH]
    var day = calendar[Calendar.DAY_OF_MONTH]

    var newYear by remember { mutableIntStateOf(year) }
    var newMonth by remember { mutableIntStateOf(month) }
    var newDay by remember { mutableIntStateOf(day) }

    val datePicker =
        DatePickerDialog(
            context,
            { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                selectedDate.set(selectedYear, selectedMonth, selectedDay)
                selectedDateText = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)
                newYear = selectedYear
                newMonth = selectedMonth + 1
                newDay = selectedDay
            },
            year,
            month,
            day,
        )
    datePicker.updateDate(newYear, newMonth, newDay)
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
            },
        ) {
            Text(text = defaultText)
        }
    }
    return LocalDateTime.of(newYear, newMonth, newDay, 0, 0)
}

@Composable
fun TasksTimePicker(): Pair<Int, Int> {
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
        horizontalAlignment = Alignment.End
    ) {
        ReadonlyOutlinedTextField(
            value = selectedTimeText,
            onValueChange = { selectedTimeText = it },
            onClick = {
                timePicker.show()
            }
        ) {
            Text(text = "Time")
        }
    }
    return Pair(hour, minute)
}

@Composable
fun AssigneeDropdown(){
    // expanded state of the Text Field
    var expanded by remember { mutableStateOf(false) }
    // temporary list of options, eventually use list of users
    val assignees = listOf("None", "Dad", "Mom", "Sister", "Brother", "Me")
    var selectedAssignee by remember { mutableStateOf("Me") }
    var textFieldSize by remember { mutableStateOf(Size.Zero)}
    // Up Icon when expanded and down icon when collapsed
    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    Column() {
        // Create an Outlined Text Field
        // with icon and not expanded
        OutlinedTextField(
            value = selectedAssignee,
            onValueChange = { selectedAssignee = it },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    // This value is used to assign to
                    // the DropDown the same width
                    textFieldSize = coordinates.size.toSize()
                },
            label = {Text("Assignee")},
            trailingIcon = {
                Icon(icon,"contentDescription",
                    Modifier.clickable { expanded = !expanded })
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current){textFieldSize.width.toDp()})
        ) {
            assignees.forEach { label ->
                DropdownMenuItem(onClick = {
                    selectedAssignee = label
                    expanded = false
                }) {
                    Text(text = label)
                }
            }
        }
    }
}


@Composable
fun TaskCreator(addTask: (Task) -> Unit, showDialog: Boolean) {
    val id = taskIdCount
    var title by remember { mutableStateOf("") }
    var dueDate: LocalDateTime? = null
    var remindTime: LocalDateTime? = null
    var notes by remember { mutableStateOf("") }
    var isCompleted = false
    taskIdCount++

    var dueTime = Pair(0, 0)
    var reminderTime = Pair(0, 0)
    Column(
        modifier = Modifier.padding(16.dp)) {
        // Title field
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Due date fields
        Row (
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ){
            dueDate = TasksDatePicker("Due Date", null)
            Spacer(modifier = Modifier.width(16.dp))
            dueTime = TasksTimePicker()
            dueDate = dueDate?.withHour(dueTime.first)?.withMinute(dueTime.second)
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Remind time fields
        Row (
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ){
            remindTime = TasksDatePicker("Reminder", null)
            Spacer(modifier = Modifier.width(16.dp))
            reminderTime = TasksTimePicker()
            remindTime = remindTime?.withHour(reminderTime.first)?.withMinute(reminderTime.second)
        }
        Spacer(modifier = Modifier.height(16.dp))
        AssigneeDropdown()
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.padding(top = 10.dp))
        Button(
            onClick = {
                val task = Task(id = id, title = title, dueDate = dueDate, remindTime = remindTime, notes = notes, isCompleted = isCompleted)
                addTask(task)
                title = ""
                dueDate = LocalDateTime.now()
                remindTime = LocalDateTime.now()
                notes = ""
                isCompleted = false
            },

            modifier = Modifier
                .align(Alignment.End),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.White,
                contentColor = Color(darkPurple)
            )
        ) {
            Text(
                "Create",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            )
        }
    }
}


@Composable
fun TaskEditor(task: Task, showDialog: Boolean) {
    var newTitle by remember { mutableStateOf(task.title) }
    var newDueDate by remember { mutableStateOf(task.dueDate) }
    var newRemindTime by remember { mutableStateOf(task.remindTime) }
    var newNotes by remember { mutableStateOf(task.notes) }
    var newIsCompleted by remember { mutableStateOf(task.isCompleted) }
    val currDueDateText = "${task.dueDate?.dayOfMonth}/${task.dueDate?.month?.value}/${task.dueDate?.year}"
    val currRemindDateText = "${task.remindTime?.dayOfMonth}/${task.remindTime?.month?.value}/${task.remindTime?.year}"

    var dueTime = Pair(0, 0)
    var reminderTime = Pair(0, 0)
    Column(
        modifier = Modifier.padding(16.dp)) {
        // Title field
        OutlinedTextField(
            value = newTitle,
            onValueChange = { newTitle = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Due date fields
        Row (
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ){
            newDueDate = TasksDatePicker("Due Date", task.dueDate)
            Spacer(modifier = Modifier.width(16.dp))
            dueTime = TasksTimePicker()
            newDueDate = newDueDate?.withHour(dueTime.first)?.withMinute(dueTime.second)
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Remind time fields
        Row (
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ){
            newRemindTime = TasksDatePicker("Reminder", task.dueDate)
            Spacer(modifier = Modifier.width(16.dp))
            reminderTime = TasksTimePicker()
            newRemindTime = newRemindTime?.withHour(reminderTime.first)?.withMinute(reminderTime.second)
        }
        Spacer(modifier = Modifier.height(16.dp))
        AssigneeDropdown()
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = newNotes,
            onValueChange = { newNotes = it },
            label = { Text("Notes") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.padding(top = 10.dp))
        Button(
            onClick = {
                task.title = newTitle
                task.dueDate = newDueDate
                task.remindTime = newRemindTime
                task.notes = newNotes
                task.isCompleted = newIsCompleted
            },

            modifier = Modifier
                .align(Alignment.End),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.White,
                contentColor = Color(darkPurple)
            )
        ) {
            Text(
                "Update",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            )
        }
    }
}


