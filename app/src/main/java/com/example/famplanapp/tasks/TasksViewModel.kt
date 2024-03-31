package com.example.famplanapp.tasks

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.famplanapp.currUser
import com.example.famplanapp.globalClasses.User
import com.example.famplanapp.firestore
import com.google.firebase.Timestamp
import com.google.firebase.firestore.toObject
import java.time.LocalDateTime


class TasksViewModel: ViewModel() {
    //private var tasksList = mutableListOf<Task>()
    var _tasksList = MutableLiveData<List<Task>>()
    val tasksList: LiveData<List<Task>> get() = _tasksList
    //var currDisplayedTasks = tasksList
    private val _currDisplayedTasks = MutableLiveData<List<Task>>(emptyList())
    val currDisplayedTasks: LiveData<List<Task>> get() = _currDisplayedTasks
    var currFilter = "My Tasks"
    private var taskIdCount = 0


    init {
        fetchTasksFromDb()
    }

    private fun fetchTasksFromDb() {
        firestore.collection("tasks")
            .get()
            .addOnSuccessListener { result ->
                val tasks = mutableListOf<Task>()
                for (document in result) {
                    val task = document.toObject<Task>()
                    tasks.add(task)
                }
                _tasksList.value = tasks
                // Now tasksList contains all the retrieved tasks
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error getting tasks", e)
            }
    }
    fun increaseId() {
        taskIdCount++
    }
    fun getTaskIdCount(): Int {
        return taskIdCount
    }

    fun addTask(task: Task) {
        val currentTasks = _tasksList.value?.toMutableList() ?: mutableListOf()
        currentTasks.add(task)
        _tasksList.value = currentTasks
        if (task.assignee?.name != "None" || task.assignee != null) {
            task.assignee?.tasksId?.add(task.id)
        }
        // update database
        firestore.collection("tasks")
            .add(task)
            .addOnSuccessListener { documentReference ->
                val taskId = documentReference.id
                task.id = taskId
                Log.d(TAG, "Task added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding task", e)
            }

        setCurrDisplayedTasks()
    }

    fun deleteTask(task: Task) {
        val currentTasks = _tasksList.value?.toMutableList() ?: mutableListOf()
        currentTasks.remove(task)
        _tasksList.value = currentTasks
        task.assignee?.tasksId?.remove(task.id)

        // update database
        firestore.collection("tasks")
            .document(task.id.toString())
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "Task deleted successfully")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error deleting task", e)
            }

        setCurrDisplayedTasks()
    }

    fun editTask(task: Task,
                 newTitle: String,
                 newDueDate: Timestamp?,
                 newRemindTime: Timestamp?,
                 newAssignee: User,
                 newNotes: String,
                 newIsCompleted: Boolean
                 ) {
        // remove task from previous assignee
        task.assignee?.tasksId?.remove(task.id)
        val newTask = Task(task.id, newTitle, newDueDate, newRemindTime, newAssignee, newNotes, newIsCompleted)

        // update all information fields
        task.title = newTitle
        task.dueDate = newDueDate
        task.remindTime = newRemindTime
        task.assignee = newAssignee
        task.notes = newNotes
        task.isCompleted = newIsCompleted

        // add task to new assignee
        if (task.assignee?.name != "None" || task.assignee != null) {
            task.assignee?.tasksId?.add(task.id)
        }

        // update database
        firestore.collection("tasks")
            .document(task.id.toString())
            .set(newTask)
            .addOnSuccessListener {
                Log.d(TAG, "Task updated successfully")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error updating task", e)
            }

        setCurrDisplayedTasks()
    }

    fun setCurrDisplayedTasks(): List<Task>? {
        val tasks = tasksList.value ?: emptyList()
        val filteredTasks = when (currFilter) {
            "My Tasks" -> tasks.filter { it.assignee?.email == currUser.email }
            "All Tasks" -> tasks
            "Unassigned" -> tasks.filter { it.assignee?.name ?: "None" == "None" }
            else -> tasks
        }
        val sortedTasks = if (filteredTasks.isNotEmpty()) {
            filteredTasks.sortedBy { it.dueDate }
        } else {
            filteredTasks
        }
        _currDisplayedTasks.value = sortedTasks
        return _currDisplayedTasks.value
    }
}