import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.famplanapp.BottomNavBar

@Composable
fun SignInButton(onClickAction: () -> Unit) {
    Button(onClick = onClickAction) {
        Text("Sign in", fontSize = 16.sp)
    }
}

@Composable
fun SignInScreen() {
    var buttonClicked by remember { mutableStateOf(false) }
    var usernameText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    Column {
        if (buttonClicked) {
            BottomNavBar()
        }else{
            Text("Username:",
                fontSize = 16.sp)
                OutlinedTextField(value = usernameText, onValueChange = { newText ->
                    usernameText = newText
                })
                Spacer(modifier = Modifier.height(16.dp))
                Text("Password:",
                    fontSize = 16.sp)
                OutlinedTextField(value = passwordText, onValueChange = { newText ->
                passwordText = newText
                })
                SignInButton {
                    buttonClicked = true
                }
        }
    }
}