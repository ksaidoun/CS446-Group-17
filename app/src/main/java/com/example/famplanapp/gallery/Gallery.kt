package com.example.famplanapp.gallery

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.famplanapp.currUser
import com.example.famplanapp.firestore
import com.example.famplanapp.storage
import com.google.firebase.Timestamp
import com.google.firebase.appcheck.internal.util.Logger.TAG
import java.time.LocalDateTime
import java.time.ZoneOffset

@Composable
fun Gallery(photos: MutableList<Int>){
    val imageName = LocalDateTime.now().toEpochSecond(ZoneOffset.MIN).toString()
    val refString = "Gallery/$imageName.jpg"
    val storageRef = storage.reference.child(refString)
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                storageRef.putFile(uri)
                updateFireStore(imageName, refString);
            }
        }
    )

    Box(modifier = Modifier
        .fillMaxSize()
        .padding(top = 60.dp, bottom = 60.dp)) {
        UserPhotoGallery()
        Box(
            modifier = Modifier
                .padding(16.dp)
                .size(56.dp)
                .background(MaterialTheme.colors.primary, CircleShape)
                .clickable {
                    launcher.launch(
                        PickVisualMediaRequest(
                            mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                }
                .align(Alignment.BottomEnd)
        ) {
            Text(
                text = "+",
                style = TextStyle(color = MaterialTheme.colors.background, fontSize = 24.sp),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

fun updateFireStore(filename: String, ref: String) {
    firestore.collection("gallery").document(filename).set(
        hashMapOf(
            "time" to Timestamp(LocalDateTime.now().toEpochSecond(ZoneOffset.MIN), 0),
            "ref" to ref,
            "user" to currUser.name
        )
    )    .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
        .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
}
