package com.example.famplanapp.gallery

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.example.famplanapp.R
import com.example.famplanapp.firestore
import com.example.famplanapp.storage
import com.google.firebase.appcheck.internal.util.Logger.TAG

@Composable
fun UserPhotoGallery() {
    var isLoading = remember { mutableStateOf(true) }
    var photos = remember { mutableListOf<ImageData>() }
    firestore.collection("gallery").get()
        .addOnSuccessListener { result ->
            for (photo in result) {
                val name = photo.id
                val time = photo.getTimestamp("time")!!
                val ref = photo.getString("ref")!!
                photos.add(ImageData(name, time, ref))
            }
            Log.d(TAG, "UserPhotoGallery: $photos")
            isLoading.value = false
        }
        .addOnFailureListener { exception ->
            Log.w(TAG, "Error getting photos.", exception)
        }
    Log.d(TAG, "Init Column")
    Column () {
        if (isLoading.value) {
            CircularProgressIndicator()
        } else {
            Log.d(TAG, "Init LazyColumn")
            LazyColumn {
                items(photos.size) { index ->
                    val photo = photos[index]
                    Log.d(TAG, "Init Image")
                    userImageItem(imageRef = photo.ref)

                }
            }
        }
    }
}

@Composable
fun userImageItem(imageRef: String) {
    val storageReference = storage.reference.child(imageRef)
    var isLoading = remember { mutableStateOf(true) }
    var downloadUri = remember { mutableStateOf("") }
    storageReference.downloadUrl.addOnSuccessListener {
        Log.d(TAG, "download url: $it")
        downloadUri.value = it.toString()
        isLoading.value = false
    }
    .addOnFailureListener() {
        Log.d(TAG, "Failed to get download url")
    }
    if (!isLoading.value) {
        Log.d(TAG, "isLoading: $isLoading")
        Log.d(TAG, "downloadUri: $downloadUri.value")
        AsyncImage(
            model = downloadUri.value,
            placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
            error = painterResource(id = R.drawable.test1),
            contentDescription = ""
        )
    } else {
        CircularProgressIndicator()
    }
}

