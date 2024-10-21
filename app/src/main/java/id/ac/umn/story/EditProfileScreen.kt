package id.ac.umn.story

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import id.ac.umn.story.ui.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid
    var user by remember { mutableStateOf<User?>(null) }
    var username by remember { mutableStateOf("") }
    var biography by remember { mutableStateOf("") }
    var nim by remember { mutableStateOf("") }

    LaunchedEffect(userId) {
        if (userId != null) {
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    user = document.toObject(User::class.java)
                    user?.let {
                        username = it.username
                        biography = it.biography
                        nim = it.nim
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("EditProfileScreen", "Error fetching user data", e)
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(painterResource(id = R.drawable.ic_back), contentDescription = "Back")
                    }
                }
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(16.dp)
        ) {
            Text("Edit Profile", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = Color.Gray,
                ),
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = biography,
                onValueChange = { biography = it },
                label = { Text("Biography") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = Color.Gray,
                ),
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = nim,
                onValueChange = { nim = it },
                label = { Text("NIM") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = Color.Gray,
                ),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    userId?.let {
                        val updatedUser = User(username, biography, nim)
                        firestore.collection("users").document(it).set(updatedUser)
                            .addOnSuccessListener {
                                Log.d("EditProfileScreen", "User profile updated successfully")
                                navController.popBackStack()
                            }
                            .addOnFailureListener { e ->
                                Log.e("EditProfileScreen", "Error updating user profile", e)
                            }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(PrimaryBlue)
            ) {
                Text("Save")
            }
        }
    }
}