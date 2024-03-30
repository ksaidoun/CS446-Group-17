import android.content.ContentValues.TAG
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

@Composable
fun SignInButton(onClickAction: () -> Unit) {
    Button(onClick = onClickAction) {
        Text("Sign in", fontSize = 16.sp)
    }
}

@Composable
fun SignUpButton(onClickAction: () -> Unit, onJoinFamilyChecked: (Boolean) -> Unit, joinFamily: Boolean, familyCodeText: String, onFamilyCodeChange: (String) -> Unit, familyId: String?) {
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
            familyId?.let { id ->
                Text(
                    "New Family ID: $id",
                    fontSize = 16.sp
                )
            }
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
    var createFamily by remember { mutableStateOf(false) }

    val familyRef = Firebase.database.getReference("families")
    val familyId = familyRef.push().key

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (signInClicked) {
            BottomNavBar()
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
                                            currentUser = User("",familyId?: "","", "", emailText, emptyList(), "#dc143c", "User", tempSettings)
                                            if (joinFamily) {
                                                joinFamilyToFirebase(database, familyCodeText, emailText)
                                            } else {
                                                createFamilyInFirebase(database, familyId,
                                                    currentUser!!
                                                )
                                            }
                                            saveUserToFirebase(database,emailText,familyId?: "")
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
                                        currentUser = User("",familyId?: "","", "", emailText, emptyList(), "#dc143c", "User", tempSettings)
                                        signInClicked = true
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

private fun saveUserToFirebase(database: FirebaseDatabase, email: String, familyId: String?) {
    val usersRef = database.getReference("users")

    val userId = email.replace(".", ",")

    val user = User(
        familyId = familyId ?: "",
        userId = userId,
        email = email
    )

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

    val appSettings = AppSettings()

    val newFamily = Family(
        id = familyId ?: "",
        settings = appSettings,
        users = listOf(currentUser)
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