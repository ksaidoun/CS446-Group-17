package com.example.famplanapp.tasks

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.DatePicker
import android.widget.Toast
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
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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
import com.example.famplanapp.globalClasses.User
import java.text.SimpleDateFormat
import java.time.DateTimeException
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Locale
import com.google.firebase.Timestamp
import java.time.ZoneId
import java.util.Date


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
// date related helper functions
fun LocalDateTime.toCalendar(): Calendar {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.YEAR, this.year)
    calendar.set(Calendar.MONTH, this.monthValue - 1) // Calendar months are zero-based
    calendar.set(Calendar.DAY_OF_MONTH, this.dayOfMonth)
    calendar.set(Calendar.HOUR_OF_DAY, this.hour)
    calendar.set(Calendar.MINUTE, this.minute)
    calendar.set(Calendar.SECOND, this.second)
    calendar.set(Calendar.MILLISECOND, this.nano / 1000000)
    return calendar
}

fun localDateTimeToTimestamp(localDateTime: LocalDateTime?): Timestamp? {
    if (localDateTime == null) return null
    val zoneId = ZoneId.systemDefault()
    val instant = localDateTime.atZone(zoneId).toInstant()
    return Timestamp(Date.from(instant))
}

fun timestampToLocalDateTime(timestamp: Timestamp?): LocalDateTime? {
    if (timestamp == null) return null
    val instant = timestamp.toDate().toInstant()
    return instant.atZone(ZoneId.systemDefault()).toLocalDateTime()
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

    var newYear by remember { mutableStateOf(year) }
    var newMonth by remember { mutableStateOf(month) }
    var newDay by remember { mutableStateOf(day) }

    if (defaultDate != null) {
        year = defaultDate.year
        month = defaultDate.monthValue
        day = defaultDate.dayOfMonth
        newYear = defaultDate.year
        newMonth = defaultDate.monthValue
        newDay = defaultDate.dayOfMonth
        selectedDate = defaultDate.toCalendar()
        selectedDateText = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)
    }

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
            day
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
    try {
        return LocalDateTime.of(newYear, newMonth, newDay, 0, 0)
    } catch (e: DateTimeException) {
        // Handle the invalid date gracefully, e.g., return null or a default date
        return defaultDate
    }
}

@Composable
fun TasksTimePicker(defaultDate: LocalDateTime? = null): Pair<Int, Int> {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    var selectedTimeText by remember { mutableStateOf("") }
    // get current hour and minute
    val hour = calendar[Calendar.HOUR_OF_DAY]
    val minute = calendar[Calendar.MINUTE]

    var newHour by remember { mutableIntStateOf(hour) }
    var newMinute by remember { mutableIntStateOf(minute) }

    if (defaultDate != null) {
        newHour = defaultDate.hour
        newMinute = defaultDate.minute
        selectedTimeText = "$newHour:$newMinute"
    }
    val timePicker = TimePickerDialog(
        context,
        { _, selectedHour: Int, selectedMinute: Int ->
            selectedTimeText = "$selectedHour:$selectedMinute"
            newHour = selectedHour
            newMinute = selectedMinute
        }, hour, minute, false
    )
    timePicker.updateTime(newHour, newMinute)

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
    return Pair(newHour, newMinute)
}

@Composable
fun AssigneeDropdown(prevAssignee: User?, assignees: MutableList<User?>): User {
    // expanded state of the Text Field
    var expanded by remember { mutableStateOf(false) }
    // track selected assignee
    var selectedAssignee by remember { mutableStateOf(prevAssignee) }
    var textFieldSize by remember { mutableStateOf(Size.Zero)}
    // up icon when expanded and down icon when collapsed
    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    Column() {
        OutlinedTextField(
            value = selectedAssignee!!.preferredName,
            onValueChange = {  },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
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
            modifier = Modifier.width(with(LocalDensity.current) { textFieldSize.width.toDp() })
        ) {
            assignees.forEach { user ->
                DropdownMenuItem(onClick = {
                    selectedAssignee = user
                    expanded = false
                }) {
                    Text(text = user!!.preferredName)
                }
            }
        }
    }
    return selectedAssignee!!
}


@Composable
fun TaskCreator(tasksViewModel: TasksViewModel, showDialog: Boolean, assignees: MutableList<User?>) {
    val id = tasksViewModel.getTaskIdCount().toString()
    var title by remember { mutableStateOf("") }
    var dueDate: Timestamp? = null
    var remindTime: Timestamp? = null
    var assignee: User? by remember { mutableStateOf(null) }
    var notes by remember { mutableStateOf("") }
    var isCompleted = false
    tasksViewModel.increaseId()

    var dueTime: Pair<Int, Int>
    var reminderTime: Pair<Int, Int>
    val context = LocalContext.current
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
            var due = TasksDatePicker("Due Date", null)
            Spacer(modifier = Modifier.width(16.dp))
            dueTime = TasksTimePicker(null)
            dueDate = localDateTimeToTimestamp(due?.withHour(dueTime.first)?.withMinute(dueTime.second))
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Remind time fields
        Row (
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ){
            var remind = TasksDatePicker("Reminder", null)
            Spacer(modifier = Modifier.width(16.dp))
            reminderTime = TasksTimePicker(null)
            remindTime = localDateTimeToTimestamp(remind?.withHour(reminderTime.first)?.withMinute(reminderTime.second))

        }
        Spacer(modifier = Modifier.height(16.dp))
        assignee = AssigneeDropdown(assignees.first(), assignees)
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
                // create task and change model
                val task = Task(
                    id = id,
                    title = title,
                    dueDate = dueDate,
                    remindTime = remindTime,
                    assignee = assignee,
                    notes = notes,
                    isCompleted = isCompleted
                )
                tasksViewModel.addTask(task)
                showToast(context, "Task created successfully")
                title = ""
                dueDate = null
                remindTime = null
                assignee = null
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
fun TaskEditor(task: Task, tasksViewModel: TasksViewModel, showDialog: Boolean, assignees: MutableList<User?>) {
    var newTitle by remember { mutableStateOf(task.title) }
    var newDueDate by remember { mutableStateOf(task.dueDate) }
    var newRemindTime by remember { mutableStateOf(task.remindTime) }
    var newAssignee by remember { mutableStateOf(task.assignee) }
    var newNotes by remember { mutableStateOf(task.notes) }
    var newIsCompleted by remember { mutableStateOf(task.isCompleted) }

    var dueTime: Pair<Int, Int>
    var reminderTime: Pair<Int, Int>
    val context = LocalContext.current
    val convertedDueDate = timestampToLocalDateTime(task.dueDate)
    val convertedRemindTime = timestampToLocalDateTime(task.remindTime)
    Column(modifier = Modifier.padding(16.dp)) {
        IconButton(
            onClick = {
                tasksViewModel.deleteTask(task)
                showToast(context, "Task deleted successfully")
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
        }
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
            var newDue = TasksDatePicker("Due Date", convertedDueDate)
            Spacer(modifier = Modifier.width(16.dp))
            dueTime = TasksTimePicker(timestampToLocalDateTime(task.dueDate))
            newDueDate = localDateTimeToTimestamp(newDue?.withHour(dueTime.first)?.withMinute(dueTime.second))
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Remind time fields
        Row (
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ){
            var newRemind = TasksDatePicker("Reminder", convertedRemindTime)
            Spacer(modifier = Modifier.width(16.dp))
            reminderTime = TasksTimePicker(timestampToLocalDateTime(task.remindTime))
            newRemindTime = localDateTimeToTimestamp(newRemind?.withHour(reminderTime.first)?.withMinute(reminderTime.second))
        }
        Spacer(modifier = Modifier.height(16.dp))
        newAssignee = AssigneeDropdown(task.assignee, assignees)
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
                tasksViewModel.editTask(task, newTitle, newDueDate, newRemindTime, newAssignee!!, newNotes, newIsCompleted)
                showToast(context, "Task updated successfully")
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

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}


