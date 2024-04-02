package com.example.famplanapp

import android.content.Context
import android.text.Layout
import android.widget.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.famplanapp.globalClasses.User
import com.example.famplanapp.ui.theme.Purple40
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.famplanapp.globalClasses.AppSettings
import com.example.famplanapp.globalClasses.Family
import com.example.famplanapp.globalClasses.FamilyOfUsers
import com.example.famplanapp.schedule.users
import com.google.firebase.firestore.QuerySnapshot

/*
add sign out
make button go back to sign screen **** important
make button go back to original sign in screen
add shared budget to database
 */

fun getFamilyUsers(querySnapshot: QuerySnapshot): MutableList<User> {
    if (!querySnapshot.isEmpty) {
        val documents = querySnapshot.documents
        documents.forEach { document ->
            val user = User(
                document.getString("userId") ?: "",
                document.getString("familyId") ?: "",
                document.getString("name") ?: "No Name",
                document.getString("preferredName") ?: "No Preference",
                document.getString("email") ?: "",
                mutableListOf(),
                document.getString("colour") ?: "",
                document.getString("role") ?: "",
                document.getString("settingId") ?: ""
            )

            val taskIds = document.get("taskIds") as? MutableList<String>
            if (taskIds != null) {
                user.tasksIds = taskIds
            }
            if(!users.contains(user)){
                users.add(user)
            }
        }
    }
    return users
}

@Composable
fun Setting(currentUser: User, navController: NavController) {

    val context = LocalContext.current

    val curId = currentUser.userId

    var name by remember { mutableStateOf(currentUser.name) }
    var preferredName by remember { mutableStateOf(currentUser.preferredName) }
    var sharedBudgetEnabled by remember { mutableStateOf(false) }
    var notificationEnabled by remember { mutableStateOf(false) }
    var saveSettings by remember { mutableStateOf(false) }


    var users by remember { mutableStateOf(mutableListOf<User>()) }

    val reference = firestore.collection("users").whereEqualTo("familyId",currentUser.familyId)

    reference.get().addOnSuccessListener { querySnapshot ->
        users = getFamilyUsers(querySnapshot)
    }
    Scaffold(
       topBar = {
            TopAppBar(
                title = { Text(text = "Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
            )
        },
        content = { paddingValues ->
            Box(
                modifier = (Modifier.padding(top = paddingValues.calculateTopPadding()))
            ) {
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                item {
                    SectionOne("${currentUser.name}") {
                        // Display current username
                        Text(
                            "Your current preferred name is: ${currentUser.preferredName}",
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Divider(modifier = Modifier.padding(vertical = 16.dp))
                        Column {
                            Spacer(modifier = Modifier.height(5.dp))
                            OutlinedTextField(
                                value = name,
                                onValueChange = { newValue ->
                                    name = newValue
                                },
                                label = { Text("Name:") }
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = preferredName,
                                onValueChange = { newValue ->
                                    preferredName = newValue
                                },
                                label = { Text("Preferred Name:") }
                            )
                        }
                    }
                }
                item {
                    Divider(modifier = Modifier.padding(vertical = 16.dp))
                }
                item {
                    // Shared Budget Text Field and Slider
                    SectionOne("Shared Budget") {
                        Text(
                            "Shared Budget For Family:",
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        if (currentUser.role == "User") {
                            Switch(
                                checked = sharedBudgetEnabled,
                                onCheckedChange = { checked ->
                                    if (currentUser.role == "Admin") {
                                        sharedBudgetEnabled = checked
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Only Admin can edit",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                },
                                enabled = false,
                            )

                        } else {
                            Switch(
                                checked = sharedBudgetEnabled,
                                onCheckedChange = { sharedBudgetEnabled = it },
                                enabled = true,
                            )
                        }
                    }
                }
                item {
                    Divider(modifier = Modifier.padding(vertical = 16.dp))
                }
                // Notification Text Field and Slider
                item {
                    SectionOne("Notifications:") {
                        Text(
                            "Receive notifications to your phone:",
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                        if (currentUser.role == "User") {
                            Switch(
                                checked = notificationEnabled,
                                onCheckedChange = { notificationEnabled = it },
                                enabled = false,
                            )
                        } else {
                            Switch(
                                checked = notificationEnabled,
                                onCheckedChange = { notificationEnabled = it },
                                enabled = true
                            )
                        }

                    }
                }
                item {
                    Divider(modifier = Modifier.padding(vertical = 16.dp))
                    Button(
                        onClick = {
                            if (curId.isNotEmpty()) { // Check if userId is not empty or null
                                updateUser(context, curId, name, preferredName, currentUser)
                                saveSettings = true
                            } else {
                                // Handle the case where userId is null or empty
                                Toast.makeText(
                                    context,
                                    "User ID is null or empty",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        },
                        shape = RoundedCornerShape(25.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text("Save", fontSize = 16.sp)
                    }

                }
                item {
                    Divider(modifier = Modifier.padding(vertical = 16.dp))
                }
                item {
                    SectionOne("Family Members") {

                        users.forEach { user ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                //horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "${user.preferredName}",
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "${user.role}",
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.weight(1f)
                                )
                                //Spacer(modifier = Modifier.height(5.dp))

                            }
                        }
                    }
                }
                item {
                    Divider(modifier = Modifier.padding(vertical = 16.dp))
                }
                item{
                    Button(
                        onClick = {
                            // Navigate back to sign-in screen
                            navController.navigate("Sign In") {
                                popUpTo(navController.graph.startDestinationRoute!!) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    ) {
                        Text("Sign Out", fontSize = 16.sp)
                    }
                }
                item {
                    Divider(modifier = Modifier.padding(vertical = 16.dp))
                }
            }
        },
//        bottomBar = {
//            BottomAppBar(
//                modifier = Modifier.fillMaxWidth()
//            ) {
//
//            }
//        },
    )
}

private fun updateUser(context: Context, userId: String, name: String, prefName : String, currentUser: User) {

    currentUser.name = name
    currentUser.preferredName = prefName

    firestore.collection("users").document(userId).update("name",name)
        .addOnSuccessListener {
            Toast.makeText(context,"Data added ",Toast.LENGTH_LONG).show()
        }
        .addOnFailureListener {
            Toast.makeText(context," Data not added ",Toast.LENGTH_LONG).show()
        }

    firestore.collection("users").document(userId).update("preferredName",prefName)
        .addOnSuccessListener {
            Toast.makeText(context,"Data added ",Toast.LENGTH_LONG).show()
        }
        .addOnFailureListener {
            Toast.makeText(context," Data not added ",Toast.LENGTH_LONG).show()
        }
}
@Composable
fun SectionOne(title: String, content: @Composable () -> Unit) {
    Column {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        content()
    }
}