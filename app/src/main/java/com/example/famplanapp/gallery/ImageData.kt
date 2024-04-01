package com.example.famplanapp.gallery

import com.google.firebase.Timestamp

data class ImageData(
    val name: String,
    val time: Timestamp,
    val ref: String,
    val uploader: String
)
