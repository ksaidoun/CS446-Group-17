package com.example.famplanapp
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters


@Composable
fun CalendarUI(currentWeekSunday: LocalDate) {
    Column {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .background(Color(lightPurple))
                    .border(0.5.dp, Color.Black)
                ,
                contentAlignment = Alignment.Center
            ){
                //Text("Time")
            }
            repeat(7) { dayIndex ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .background(Color(lightPurple))
                        .border(0.5.dp, Color.Black)
                    ,
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = getDayAbbreviation(dayIndex) + "\n" + (currentWeekSunday.dayOfMonth + dayIndex).toString(),
                        //fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        LazyColumn(Modifier.fillMaxSize().padding(bottom = 50.dp)) {
            items(24) { rowIndex ->
                Row(Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .background(Color(lightPurple))
                            .border(0.5.dp, Color.Black)
                    ){
                        Text(if(rowIndex == 0) "12AM" else (rowIndex % 12).toString() + if(rowIndex < 12) "AM" else "PM")
                    }
                    repeat(7) { columnIndex ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .background(Color.White)
                                .border(0.5.dp, Color.Black)
                        ) {
                        }
                    }
                }
            }
        }
    }
}

fun getDayAbbreviation(dayIndex: Int): String {
    return when (dayIndex) {
        0 -> "Sun"
        1 -> "Mon"
        2 -> "Tue"
        3 -> "Wed"
        4 -> "Thu"
        5 -> "Fri"
        6 -> "Sat"
        else -> throw IllegalArgumentException("Invalid day index: $dayIndex")
    }
}
@Composable
fun Schedule(innerPadding: PaddingValues) {
    var currentWeekSunday by remember { mutableStateOf(LocalDate.now().with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.SUNDAY))) }
    println(currentWeekSunday.toString())
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Week starting from: ${currentWeekSunday.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}",
            style = MaterialTheme.typography.h5
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { currentWeekSunday = currentWeekSunday.plusWeeks(-1) },
            modifier = Modifier.fillMaxWidth()
        ){
            Text("Previous Week", style = TextStyle(
                    fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            ))
        }
        Button(
            onClick = { currentWeekSunday = currentWeekSunday.plusWeeks(1) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Next Week", style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            ))
        }
        CalendarUI(currentWeekSunday)
    }

}