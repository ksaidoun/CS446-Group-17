package com.example.famplanapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.util.Calendar
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
@Composable
fun Setting(innerPadding: PaddingValues) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(70.dp))
        Row(
            modifier = Modifier
                .padding(start = 10.dp)
                .fillMaxWidth()
                .height(120.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(shape = RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colors.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "test1"
                )
            }
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(shape = RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colors.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "test2"
                )
            }
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(shape = RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colors.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "test3"
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .padding(start = 10.dp)
                .fillMaxWidth()
                .height(120.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(shape = RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colors.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "test1"
                )
            }
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(shape = RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colors.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "test2"
                )
            }
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(shape = RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colors.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "test3"
                )
            }
        }
    }
}