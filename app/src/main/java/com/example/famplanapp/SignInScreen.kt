package com.example.famplanapp

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
//import com.example.famplanapp.BottomNavBar
import com.google.firebase.auth.FirebaseAuth
import com.example.famplanapp.globalClasses.AppSettings
import com.example.famplanapp.globalClasses.Family
import com.example.famplanapp.globalClasses.User
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FieldValue


var currUser = User()

@Composable
fun SignInButton(onClickAction: () -> Unit) {
    Button(onClick = onClickAction) {
        Text("Sign in", fontSize = 16.sp)
    }
}

@Composable
fun SignUpButton(onClickAction: () -> Unit, onJoinFamilyChecked: (Boolean) -> Unit, joinFamily: Boolean, familyCodeText: String, onFamilyCodeChange: (String) -> Unit, familyId: String) {
    Column {
        Button(onClick = onClickAction) {
            Text("Sign up", fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = joinFamily,
                onCheckedChange = { checked ->
                    onJoinFamilyChecked(checked)
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Join a family")
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (joinFamily) {
            Text(
                "Family Code:",
                fontSize = 16.sp
            )
            OutlinedTextField(
                value = familyCodeText,
                onValueChange = { newText ->
                    onFamilyCodeChange(newText)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }else {
                Text(
                    "New Family ID: $familyId",
                    fontSize = 16.sp
                )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun SignInScreen() {
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current
    val database = Firebase.database

    var signInClicked by remember { mutableStateOf(false) }
    var signUpClicked by remember { mutableStateOf(false) }
    var emailText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    var verifyPasswordText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var joinFamily by remember { mutableStateOf(false) }
    var familyCodeText by remember { mutableStateOf("") }

    var familyId = ""
    var uid = ""
    var settingsId = ""

    val query = firestore.collection("families")
    val countQuery = query.count()
    countQuery.get(AggregateSource.SERVER).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            // Count fetched successfully
            val snapshot = task.result
            val c = snapshot.count + 1
            familyId = "family" + c.toString()
            settingsId = "setting" + c.toString()
            Log.d(TAG, "Count: ${snapshot.count}")
        } else {
            Log.d(TAG, "Count failed: ", task.getException())
        }
    }

    val queryU = firestore.collection("users")
    val countQueryU = queryU.count()
    countQueryU.get(AggregateSource.SERVER).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            // Count fetched successfully
            val snapshot = task.result
            var c = snapshot.count + 1
            uid = "user" + c.toString()
            Log.d(TAG, "Count: ${snapshot.count}")
        } else {
            Log.d(TAG, "Count failed: ", task.getException())
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (signInClicked) {
            currUser?.let{BottomNavBar(it)}
        } else {
            Image(
                painter = painterResource(id = R.drawable.logowname),
                contentDescription = "Logo",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (signUpClicked) {
                Text(
                    "Email:",
                    fontSize = 16.sp
                )
                OutlinedTextField(
                    value = emailText,
                    onValueChange = { newText ->
                        emailText = newText
                        errorMessage = null
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Password:",
                    fontSize = 16.sp
                )
                OutlinedTextField(
                    value = passwordText,
                    onValueChange = { newText ->
                        passwordText = newText
                        errorMessage = null
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Verify Password:",
                    fontSize = 16.sp
                )
                OutlinedTextField(
                    value = verifyPasswordText,
                    onValueChange = { newText ->
                        verifyPasswordText = newText
                        errorMessage = null
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                errorMessage?.let { message ->
                    Text(
                        message,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                SignUpButton(
                    onClickAction = {
                        if (passwordText == verifyPasswordText) {
                            if (isValidEmail(emailText) && isValidPassword(passwordText)) {
                                auth.createUserWithEmailAndPassword(emailText, passwordText)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {

                                            if (joinFamily) {
                                                settingsId = "setting" + familyCodeText.replace(Regex("[^0-9]"), "")
                                                val user = User(uid, familyCodeText,"No Name", "No Preference", emailText, mutableListOf(), "#dc143c", "User",settingsId )
                                                currUser = user
                                                saveUserAndJoinFamilyToFirebase(context,user,familyCodeText)
                                            } else {
                                                val user = User(uid, familyId,"No Name", "No Preference", emailText, mutableListOf(), "#dc143c", "Admin",settingsId )
                                                currUser = user
                                                createFamilyAndSaveUser(context, user)
                                            }
                                            signInClicked = true
                                        } else {
                                            Log.e(TAG, "createUserWithEmailAndPassword failed: ${task.exception}")
                                            errorMessage = "Failed to create user: ${task.exception?.message}"
                                        }
                                    }
                            } else {
                                if(!isValidEmail(emailText)){
                                    errorMessage = "Invalid email format."
                                }else {
                                    errorMessage = "Invalid password format."
                                }
                            }
                        } else {
                            errorMessage = "Passwords do not match."
                        }
                    },
                    onJoinFamilyChecked = { checked -> joinFamily = checked },
                    joinFamily = joinFamily,
                    familyCodeText = familyCodeText,
                    onFamilyCodeChange = { newText -> familyCodeText = newText },
                    familyId = familyId
                )
            } else {
                Text(
                    "Email:",
                    fontSize = 16.sp
                )
                OutlinedTextField(
                    value = emailText,
                    onValueChange = { newText ->
                        emailText = newText
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Password:",
                    fontSize = 16.sp
                )
                OutlinedTextField(
                    value = passwordText,
                    onValueChange = { newText ->
                        passwordText = newText
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    SignInButton {
                        if(emailText.isNotEmpty() && passwordText.isNotEmpty()){
                            auth.signInWithEmailAndPassword(emailText, passwordText)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        firestore.collection("users").whereEqualTo("email",emailText).get()


                                        val reference = firestore.collection("users").whereEqualTo("email",emailText)

                                        reference.get().addOnSuccessListener { querySnapshot ->
                                            if (!querySnapshot.isEmpty) {
                                                val document = querySnapshot.documents[0]
                                                val user = User(
                                                    document.getString("userId") ?: "error",
                                                    document.getString("familyId") ?: "error",
                                                    document.getString("name") ?: "error",
                                                    document.getString("preferredName") ?: "error",
                                                    document.getString("email") ?: "error",
                                                    mutableListOf(),
                                                    document.getString("colour") ?: "error",
                                                    document.getString("role") ?: "error",
                                                    document.getString("settingId") ?: "error"
                                                )

                                                val taskIds = document.get("taskIds") as? MutableList<String>
                                                if (taskIds != null) {
                                                    user.tasksIds = taskIds
                                                }
                                                currUser = user
                                                signInClicked = true
                                            }else{
                                                val user = User(uid, familyId,"No Name", "No Preference", emailText, mutableListOf(), "#dc143c", "User", settingsId)
                                                currUser = user
                                                signInClicked = true
                                            }
                                        }
                                    } else {
                                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                                        Toast.makeText(
                                            context, "Authentication failed.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                        }else{
                            Toast.makeText(
                                context, "Email and password cannot be empty.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(onClick = { signUpClicked = true }) {
                        Text("New User", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

private fun createFamilyAndSaveUser(context: Context, user: User) {
    val familyId = user.familyId

    val newSettings = AppSettings(user.settingId,false,"")

    val newFamily = Family(familyId = familyId, userIds = mutableListOf(user.userId), settingsId = user.settingId)

    firestore.collection("users").document(user.userId).set(user)
        .addOnSuccessListener {
            Toast.makeText(context,"Data added ",Toast.LENGTH_LONG).show()
        }
        .addOnFailureListener {
            Toast.makeText(context," Data not added ",Toast.LENGTH_LONG).show()
        }

    firestore.collection("families").document(familyId).set(newFamily)
        .addOnSuccessListener {
            Toast.makeText(context,"Data added ",Toast.LENGTH_LONG).show()
        }
        .addOnFailureListener {
            Toast.makeText(context," Data not added ",Toast.LENGTH_LONG).show()
        }

    firestore.collection("settings").document(user.settingId).set(newSettings)
        .addOnSuccessListener {
            Toast.makeText(context,"Data added ",Toast.LENGTH_LONG).show()
        }
        .addOnFailureListener {
            Toast.makeText(context," Data not added ",Toast.LENGTH_LONG).show()
        }
}

private fun saveUserAndJoinFamilyToFirebase(context: Context, user: User, familyCode: String, ) {
    firestore.collection("users").document(user.userId).set(user)
        .addOnSuccessListener {
            Toast.makeText(context,"Data added ",Toast.LENGTH_LONG).show()
        }
        .addOnFailureListener {
            Toast.makeText(context," Data not added ",Toast.LENGTH_LONG).show()
        }

    firestore.collection("families").document(user.familyId).update("userIds", FieldValue.arrayUnion(user.userId))
        .addOnSuccessListener {
            Toast.makeText(context, "User added to family", Toast.LENGTH_LONG).show()
        }
        .addOnFailureListener { exception ->
            Toast.makeText(context, "Failed to add user to family: $exception", Toast.LENGTH_LONG).show()
        }
}


private fun isValidEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

private fun isValidPassword(password: String): Boolean {
    return password.length >= 6
}

/*
Add 5 hours for
very rough layout ready
got name and prefName database update to work
got basic setting editing done
 */