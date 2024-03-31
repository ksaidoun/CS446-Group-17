package com.example.famplanapp.gallery

import android.content.Context
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.famplanapp.R
import com.example.famplanapp.firestore
import com.example.famplanapp.storage
import com.google.firebase.appcheck.internal.util.Logger.TAG
import java.io.File

@Composable
fun UserPhotoGallery() {
    var isLoading = remember { mutableStateOf(true) }
    var photos = remember { mutableListOf<ImageData>() }
    var prevPhotosSize = 0
    firestore.collection("gallery").get()
        .addOnSuccessListener { result ->
            if (result.size() > prevPhotosSize) {
                photos.clear()
                for (photo in result) {
                    val name = photo.id
                    val time = photo.getTimestamp("time")!!
                    val ref = photo.getString("ref")!!
                    val uploader = photo.getString("user")!!
                    photos.add(ImageData(name, time, ref, uploader))
                }
                Log.d(TAG, "UserPhotoGallery: $photos")
                prevPhotosSize = result.size()
                isLoading.value = false
            } else {
                Log.d(TAG, "No new photos")
                isLoading.value = false
            }
        }
        .addOnFailureListener { exception ->
            Log.w(TAG, "Error getting photos.", exception)
        }
    Log.d(TAG, "Init Column")
    if (isLoading.value) {
        CircularProgressIndicator()
    } else {
        if (photos.isEmpty()) {
            Text("No photos uploaded yet")
        } else {
            var selectedPhoto by remember { mutableStateOf<ImageData?>(null) }
            Log.d(TAG, "Init LazyColumn")
            LazyColumn (
                contentPadding = PaddingValues(start = 8.dp, end = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(photos.chunked(3)) { index, chunkedList ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        chunkedList.forEach {
                            Log.d(TAG, "Init Image")
                            userImageItem(image = it) {
                                selectedPhoto = it
                            }
                        }
                    }
                }
            }
            selectedPhoto?.let {
                UserFullSizeImageModal(
                    photo = it,
                    onClose = { selectedPhoto = null }
                )
            }
        }
    }
}

@Composable
fun userImageItem(image: ImageData, onClick: (ImageData) -> Unit){
    val storageReference = storage.reference.child(image.ref)
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
    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(shape = RoundedCornerShape(8.dp))
            .background(MaterialTheme.colors.primary)
            .clickable { onClick(image) },
        contentAlignment = Alignment.Center
    ) {
        if (!isLoading.value) {
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                model = downloadUri.value,
                placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                error = painterResource(id = R.drawable.test1),
                contentDescription = ""
            )
        } else {
            CircularProgressIndicator()
        }
    }
//    if (!isLoading.value) {
//        Log.d(TAG, "isLoading: $isLoading")
//        Log.d(TAG, "downloadUri: $downloadUri.value")
//        AsyncImage(
//            model = downloadUri.value,
//            placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
//            error = painterResource(id = R.drawable.test1),
//            contentDescription = ""
//        )
//    } else {
//        CircularProgressIndicator()
//    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UserFullSizeImageModal(photo: ImageData, onClose: () -> Unit) {
    val storageReference = storage.reference.child(photo.ref)
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
    val uploadDate = photo.time.toDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate()
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Expanded)

    LaunchedEffect(sheetState) {
        snapshotFlow { sheetState.isVisible }
            .collect { isVisible ->
                if (!isVisible) {
                    onClose()
                }
            }
    }

    ModalBottomSheetLayout(
        sheetContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(MaterialTheme.colors.background)
                    .padding(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .align(Alignment.TopCenter)
                        .width(50.dp)
                        .height(8.dp)
                        .background(MaterialTheme.colors.primary)
                )
                if (!isLoading.value) {
                    AsyncImage(
                        alignment = Alignment.TopCenter,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                        contentScale = ContentScale.Inside,
                        model = downloadUri.value,
                        placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                        error = painterResource(id = R.drawable.test1),
                        contentDescription = ""
                    )
                } else {
                    CircularProgressIndicator()
                }
//                Image(
//                    alignment = Alignment.TopCenter,
//                    painter = painterResource(id = photoResId),
//                    contentDescription = null,
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(top = 16.dp, start = 16.dp, end = 16.dp),
//                    contentScale = ContentScale.Inside
//
//                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(MaterialTheme.colors.background)
            ) {
                Text(
                    text = "Uploader: ${photo.uploader}\nDate: $uploadDate",
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier
                        .padding(16.dp)
                )
                Button(
                    onClick = { userSaveImageToLocal(context, photo) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                ) {
                    Text("Download")
                }
            }
        },
        sheetState = sheetState,
        sheetShape = MaterialTheme.shapes.large,
        sheetBackgroundColor = Color.Transparent,
        sheetElevation = 16.dp,
        scrimColor = Color.Black.copy(alpha = 0.6f)
    ) {
    }
}

private fun userSaveImageToLocal(context: Context, photo: ImageData) {
    val storageReference = storage.reference.child(photo.ref)
    val filename = "image_${System.currentTimeMillis()}.jpg"
    val filepath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val file = File(filepath, filename)
    storageReference.getFile(file).addOnSuccessListener {
        Toast.makeText(context, "Downloaded to $filepath/$filename", Toast.LENGTH_SHORT).show()
    }.addOnFailureListener() {
        Toast.makeText(context, "Failed to download", Toast.LENGTH_SHORT).show()
    }
}