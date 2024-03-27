package com.example.famplanapp.tasks

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.example.famplanapp.currUser
import com.example.famplanapp.globalClasses.User
import java.time.LocalDateTime

class TasksViewModel: ViewModel() {
    var tasksList = mutableListOf<Task>()
    var currDisplayedTasks = tasksList.toMutableStateList()
    var currFilter = "My Tasks"
    var onUpdate = mutableStateOf(0)

    fun addTask(task: Task) {
        tasksList.add(task)
        if (task.assignee?.name != "None" || task.assignee != null) {
            task.assignee?.tasks?.add(task)
        }
    }

    fun deleteTask(task: Task) {
        tasksList.remove(task)
        task.assignee?.tasks?.remove(task)
    }

    fun editTask(task: Task,
                 newTitle: String,
                 newDueDate: LocalDateTime,
                 newRemindTime: LocalDateTime,
                 newAssignee: User,
                 newNotes: String,
                 newIsCompleted: Boolean
                 ) {
        // remove task from previous assignee
        task.assignee?.tasks?.remove(task)
        // update all information fields
        task.title = newTitle
        task.dueDate = newDueDate
        task.remindTime = newRemindTime
        task.assignee = newAssignee
        task.notes = newNotes
        task.isCompleted = newIsCompleted
        // add task to new assignee
        if (task.assignee?.name != "None" || task.assignee != null) {
            task.assignee?.tasks?.add(task)
        }
    }

    fun setCurrDisplayedTasks(): MutableList<Task> {
        currDisplayedTasks.clear()
        when (currFilter) {
            "My Tasks" -> {
                currDisplayedTasks = tasksList.filter { it.assignee?.email == currUser.email }.toMutableStateList()
            }
            "All Tasks" -> {
                currDisplayedTasks = tasksList.toMutableStateList()
            }
            "Unassigned" -> {
                currDisplayedTasks = tasksList.filter { it.assignee?.name == "None" }.toMutableStateList()
            }
            else -> {
                currDisplayedTasks = tasksList.toMutableStateList()
            }
        }
        return currDisplayedTasks
    }

    private fun updateUI() {
        onUpdate.value = (0..1_000_000).random()
    }
    fun update(){
        updateUI()
    }
}