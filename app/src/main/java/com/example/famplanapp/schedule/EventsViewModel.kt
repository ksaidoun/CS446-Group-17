package com.example.famplanapp.schedule

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.famplanapp.firestore
import com.google.firebase.firestore.toObject

class EventsViewModel: ViewModel(){
    private var _eventList = MutableLiveData<List<Event>>()
    val eventList: LiveData<List<Event>> get() = _eventList
    private var eventIdCount = 0
    private fun fetchEventsFromDb() {
        firestore.collection("events")
            .get()
            .addOnSuccessListener { result ->
                val events = mutableListOf<Event>()
                for (document in result) {
                    val event = document.toObject<Event>()
                    events.add(event)
                }
                _eventList.value = events
                // Now eventList contains all the retrieved events
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error getting events", e)
            }
    }
    init { fetchEventsFromDb()}

    fun increaseId() {
        eventIdCount++
    }
    fun getEventIdCount(): Int {
        return eventIdCount
    }

    fun addEvent(event: Event) {
        val documentReference = firestore.collection("events").document()
        event.id = documentReference.id // Assign the document reference id to event id

        // update database
        documentReference
            .set(event)
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "Event added with ID: ${documentReference.id}")

                val currentEvents = _eventList.value?.toMutableList() ?: mutableListOf()
                currentEvents.add(event)
                _eventList.value = currentEvents
                /*if (event.title != "") {
                    event.assignee?.tasksIds?.add(task.id)
                }*/
                //setCurrDisplayedTasks()
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding event", e)
            }
        //setCurrDisplayedTasks()
    }
}