import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDate

@Composable
fun PhotoGallery(photos: List<Int>) {
    var selectedPhoto by remember { mutableStateOf<Int?>(null) }

    LazyColumn(
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
                chunkedList.forEach { photoResId ->
                    PhotoItem(photoResId) { selectedPhoto = it }
                }
            }
        }
    }

    selectedPhoto?.let { photoResId ->
        FullSizeImageModal(
            photoResId = photoResId,
            onClose = { selectedPhoto = null }
        )
    }
}

@Composable
fun PhotoItem(photoResId: Int, onClick: (Int) -> Unit) {
    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(shape = RoundedCornerShape(8.dp))
            .background(MaterialTheme.colors.primary)
            .clickable { onClick(photoResId) },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = photoResId),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FullSizeImageModal(photoResId: Int, onClose: () -> Unit) {
    val currentDate = LocalDate.now()
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
                Image(
                    alignment = Alignment.TopCenter,
                    painter = painterResource(id = photoResId),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                    contentScale = ContentScale.Inside

                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(MaterialTheme.colors.background)
            ) {
                Text(
                    text = "Uploader: Brandon\nDate: $currentDate",
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier
                        .padding(16.dp)
                )
                Button(
                    onClick = { saveImageToLocal(context, photoResId) },
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
        scrimColor = Color.Black.copy(alpha = 0.6f)
    ) {
    }
}
private fun saveImageToLocal(context: Context, photoResId: Int) {
    val bitmap = (context.resources.getDrawable(photoResId) as BitmapDrawable).bitmap
    val filename = "image_${System.currentTimeMillis()}.jpg"
    val filepath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
    val file = File(filepath, filename)

    try {
        val stream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        stream.flush()
        stream.close()
        Toast.makeText(context, "Image saved successfully", Toast.LENGTH_SHORT).show()
    } catch (e: IOException) {
        e.printStackTrace()
        Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
    }
}