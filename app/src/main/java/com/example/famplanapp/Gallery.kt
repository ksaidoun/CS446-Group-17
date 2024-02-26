import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun PhotoGallery(photos: List<Int>) {
    LazyColumn(
        contentPadding = PaddingValues(start = 8.dp, top = 80.dp, end = 8.dp, bottom = 80.dp),
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
                    PhotoItem(photoResId)
                }
            }
        }
    }
}

@Composable
fun PhotoItem(photoResId: Int) {
    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(shape = MaterialTheme.shapes.medium)
            .background(Color.LightGray)
            .clickable { /* Include Full Image View Here */ },
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