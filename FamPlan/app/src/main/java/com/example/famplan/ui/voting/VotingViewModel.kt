package com.example.famplan.ui.voting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class VotingViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is voting Fragment"
    }
    val text: LiveData<String> = _text
}