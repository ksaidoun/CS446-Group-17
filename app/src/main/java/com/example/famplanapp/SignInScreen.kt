import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import com.example.famplanapp.BottomNavBar

@Composable
fun SignInButton(onClickAction: () -> Unit) {
    Button(onClick = onClickAction) {
        Text("Sign in")
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
            Text("Username:")
            TextField(value = usernameText, onValueChange = { newText ->
                usernameText = newText })
            Text("Password:")
            TextField(value = passwordText, onValueChange = { newText ->
                passwordText = newText })
            SignInButton {
                buttonClicked = true
            }
        }
    }
}