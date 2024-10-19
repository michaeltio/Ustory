package id.ac.umn.story

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import id.ac.umn.story.ui.theme.PrimaryBlue

@Composable
fun RegisterScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var nim by remember { mutableStateOf("") }
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ustory),
                contentDescription = "UStory Logo",
                modifier = Modifier.size(128.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Register to UStory",
                style = MaterialTheme.typography.headlineLarge,
                color = PrimaryBlue,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = nim,
                onValueChange = { nim = it },
                label = { Text("NIM") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || username.isEmpty() || nim.isEmpty()) {
                        Toast.makeText(context, "All fields must be filled", Toast.LENGTH_SHORT).show()
                    } else if (password != confirmPassword) {
                        Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                    } else {
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val userId = auth.currentUser?.uid
                                    val user = hashMapOf(
                                        "username" to username,
                                        "nim" to nim,
                                        "biography" to ""
                                    )
                                    userId?.let {
                                        firestore.collection("users").document(it).set(user)
                                            .addOnSuccessListener {
                                                navController.navigate("home") {
                                                    popUpTo("register") { inclusive = true }
                                                }
                                            }
                                            .addOnFailureListener {
                                                Toast.makeText(context, "Failed to save user data", Toast.LENGTH_SHORT).show()
                                            }
                                    }
                                } else {
                                    Toast.makeText(context, "Registration failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(PrimaryBlue)
            ) {
                Text(text = "Register", color = Color.White)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Already have an account? Login",
                color = PrimaryBlue,
                modifier = Modifier.clickable { navController.navigate("login") }
            )
        }
    }
}