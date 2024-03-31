import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Color
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
import com.example.famplanapp.*
import com.example.famplanapp.R
import com.google.firebase.auth.FirebaseAuth
import com.example.famplanapp.globalClasses.AppSettings
import com.example.famplanapp.globalClasses.Family
import com.example.famplanapp.globalClasses.User
import com.example.famplanapp.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.example.famplanapp.firestore
import com.google.firebase.firestore.AggregateSource

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
    var currentUser by remember {mutableStateOf<User?>(null)}

    var familyId = ""
    var uid = ""

    val query = firestore.collection("families")
    val countQuery = query.count()
    countQuery.get(AggregateSource.SERVER).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            // Count fetched successfully
            val snapshot = task.result
            var c = snapshot.count + 1
            familyId = "family" + c.toString()
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
            currentUser?.let{BottomNavBar(it)}
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
                                            val user = User(uid, familyId,"", "", emailText, mutableListOf(), "#dc143c", "User", )
                                            if (joinFamily) {
                                                joinFamilyToFirebase(database, familyCodeText, emailText)
                                                saveUserToFirebase(database,user)
                                            } else {
                                                currentUser = user
                                                createFamilyAndSaveUser(database, context, user)
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
                                        val userId = emailText.replace(".", ",")
                                        val userRef = database.getReference("users").child(userId)
                                        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                val user = snapshot.getValue(User::class.java)
                                                currentUser = user
                                                signInClicked = true
                                            }

                                            override fun onCancelled(error: DatabaseError) {
                                                // Handle database error
                                            }
                                        })
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

private fun createFamilyAndSaveUser(database: FirebaseDatabase, context: Context, user: User) {
    val familyId = user.familyId

    val newFamily = Family(familyId = familyId, userIds = mutableListOf(user.userId), settingsId = "")

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
}

private fun saveUserToFirebase(database: FirebaseDatabase, user: User) {
    val userId = user.email.replace(".", ",")
    val usersRef = database.getReference("users")
    usersRef.child(userId).setValue(user)
        .addOnSuccessListener {
            Log.d(TAG, "User data saved successfully")
        }
        .addOnFailureListener { exception ->
            Log.e(TAG, "Error saving user data: $exception")
        }
}

private fun createFamilyInFirebase(database: FirebaseDatabase, familyId: String?, currentUser: User) {
    val familyRef = database.getReference("families")

    val newFamily = Family(
        familyId = familyId ?: "",
        settingsId = "",
        userIds = mutableListOf(currentUser.userId)
    )

    familyRef.child(familyId ?: "").setValue(newFamily)
        .addOnSuccessListener {
            Log.d(TAG, "Family created successfully")
        }
        .addOnFailureListener { exception ->
            Log.e(TAG, "Error creating family: $exception")
        }
}

private fun joinFamilyToFirebase(database: FirebaseDatabase, familyCode: String, userEmail: String) {
    val familyRef = database.getReference("families")

    familyRef.orderByChild("familyCode").equalTo(familyCode)
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val familyId = snapshot.children.first().key.toString()

                    familyRef.child(familyId).child("members").push().setValue(userEmail)
                        .addOnSuccessListener {
                            Log.d(TAG, "User added to family successfully")
                        }
                        .addOnFailureListener { exception ->
                            Log.e(TAG, "Error adding user to family: $exception")
                        }
                } else {
                    Log.e(TAG, "Family with provided code not found")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error querying family: $error")
            }
        })
}

private fun isValidEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

private fun isValidPassword(password: String): Boolean {
    return password.length >= 6
}

/*
Next steps for Lauren:
- modify Navigation.kt so that in the dropdownmenu displays the familyMembers and retrieved from firebase families
  with the same familyId as the currentUser from the SignInScreen.kt
- figure out how to add user and families to database 
- Add 2 hours to timelog for: got start of adding users and families to database working but need to create those tables in the database first
 */