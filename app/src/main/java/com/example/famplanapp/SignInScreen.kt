import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
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
fun SignInScreen() {
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    var buttonClicked by remember { mutableStateOf(false) }
    var usernameText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (buttonClicked) {
            BottomNavBar()
        } else {
            Image(
                painter = painterResource(id = R.drawable.logowname),
                contentDescription = "Logo",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Username:",
                fontSize = 16.sp
            )
            OutlinedTextField(
                value = usernameText,
                onValueChange = { newText ->
                    usernameText = newText
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
            SignInButton {
                auth.signInWithEmailAndPassword(usernameText, passwordText)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            buttonClicked = true
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.exception)
                            Toast.makeText(
                                context, "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
    }

}