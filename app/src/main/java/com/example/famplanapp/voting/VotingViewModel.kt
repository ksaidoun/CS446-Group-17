package com.example.famplanapp.voting

import android.util.Log
import com.example.famplanapp.firestore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.toObject
import android.content.ContentValues.TAG
import com.google.firebase.Timestamp

class VotingViewModel: ViewModel() {

    private var _pollsList = MutableLiveData<List<Poll>>()
    val pollsList: LiveData<List<Poll>> get() = _pollsList

    init {
        fetchPollsFromDb()
    }

    private fun fetchPollsFromDb() {
        firestore.collection("polls")
            .get()
            .addOnSuccessListener { result ->
                val polls = mutableListOf<Poll>()
                for (document in result) {
                    val poll = document.toObject<Poll>()
                    polls.add(poll)
                }
                _pollsList.value = sortPolls(polls)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error getting polls", e)
            }
    }

    fun addPoll(poll: Poll) {
        val documentReference = firestore.collection("polls").document()
        val pollData = hashMapOf(
            "owner" to poll.owner,
            "subject" to poll.subject,
            "options" to poll.options.map { option ->
                hashMapOf("option" to option.option, "votes" to option.votes)
            },
            "deadline" to poll.deadline
        )

        documentReference
            .set(pollData)
            .addOnSuccessListener {
                val updatedPolls = (_pollsList.value ?: mutableListOf()).toMutableList()
                updatedPolls.add(poll)
                _pollsList.value = sortPolls(updatedPolls)
                Log.d(TAG, "Poll added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding poll", e)
            }
    }


    private fun sortPolls(polls: List<Poll>): List<Poll> {
        val now = Timestamp.now()

        val (upcomingPolls, pastPolls) = polls.partition { it.deadline?.compareTo(now) ?: -1 > 0 }
        val sortedUpcomingPolls = upcomingPolls.sortedBy { it.deadline }
        val sortedPastPolls = pastPolls.sortedByDescending { it.deadline }
        return sortedUpcomingPolls + sortedPastPolls
    }

    fun voteOption(pollId: String, optionIndex: Int) {
        val pollDocRef = firestore.collection("polls").document(pollId)

        firestore.runTransaction { transaction ->
            val pollSnapshot = transaction.get(pollDocRef)
            val poll = pollSnapshot.toObject<Poll>()
            if (poll != null) {
                poll.options.get(optionIndex).votes += 1
            }
            transaction.set(pollDocRef, poll!!)
        }.addOnSuccessListener {
            Log.d(TAG, "Vote successfully added to option")
        }.addOnFailureListener { e ->
            Log.w(TAG, "Error updating vote count", e)
        }
    }
}