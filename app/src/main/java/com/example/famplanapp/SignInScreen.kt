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

@Composable
fun SignInButton(onClickAction: () -> Unit) {
    Button(onClick = onClickAction) {
        Text("Sign in", fontSize = 16.sp)
    }
}

@Composable
fun SignUpButton(onClickAction: () -> Unit, onJoinFamilyChecked: (Boolean) -> Unit, joinFamily: Boolean, familyCodeText: String, onFamilyCodeChange: (String) -> Unit) {
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
        }
    }
}


@Composable
fun SignInScreen() {
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    var signInClicked by remember { mutableStateOf(false) }
    var signUpClicked by remember { mutableStateOf(false) }
    var emailText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    var verifyPasswordText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var joinFamily by remember { mutableStateOf(false) }
    var familyCodeText by remember { mutableStateOf("") }

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
                    onFamilyCodeChange = { newText -> familyCodeText = newText }
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

private fun isValidEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

private fun isValidPassword(password: String): Boolean {
    return password.length >= 6
}
