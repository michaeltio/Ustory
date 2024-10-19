package id.ac.umn.story

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import id.ac.umn.story.ui.theme.PrimaryBlue
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreen(navController: NavController) {
    var caption by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()
    val user = auth.currentUser
    val username = remember { mutableStateOf("") }

    LaunchedEffect(user) {
        user?.let {
            val userId = it.uid
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        username.value = document.getString("username") ?: "User"
                    }
                }
                .addOnFailureListener {
                    username.value = "User"
                }
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Post") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryBlue,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = { BottomNavBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BasicTextField(
                value = caption,
                onValueChange = { caption = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(8.dp),
                decorationBox = { innerTextField ->
                    if (caption.isEmpty()) {
                        Text("Write a caption...", color = Color.Gray)
                    }
                    innerTextField()
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(PrimaryBlue)
            ) {
                Text("Pick an Image", color = Color.White)
            }
            imageUri?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = null,
                    modifier = Modifier.size(200.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (caption.isEmpty()) {
                        Toast.makeText(context, "Caption cannot be empty", Toast.LENGTH_SHORT).show()
                    } else {
                        val userId = auth.currentUser?.uid
                        if (userId == null) {
                            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val postId = UUID.randomUUID().toString()
                        val post = hashMapOf<String, Any>(
                            "postId" to postId,
                            "userId" to userId,
                            "username" to username.value,
                            "caption" to caption,
                            "likes" to 0
                        )
                        if (imageUri != null) {
                            val imageRef = storage.reference.child("posts/$postId.jpg")
                            imageRef.putFile(imageUri!!)
                                .addOnSuccessListener {
                                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                                        post["imageUrl"] = uri.toString()
                                        savePostToFirestore(postId, post, firestore, context, navController)
                                    }
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            savePostToFirestore(postId, post, firestore, context, navController)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(PrimaryBlue)
            ) {
                Text("Upload", color = Color.White)
            }
        }
    }
}

private fun savePostToFirestore(
    postId: String,
    post: HashMap<String, Any>,
    firestore: FirebaseFirestore,
    context: android.content.Context,
    navController: NavController
) {
    firestore.collection("posts").document(postId).set(post)
        .addOnSuccessListener {
            Toast.makeText(context, "Post uploaded successfully", Toast.LENGTH_SHORT).show()
            navController.navigate("home") {
                popUpTo("post") { inclusive = true }
            }
        }
        .addOnFailureListener {
            Toast.makeText(context, "Post upload failed", Toast.LENGTH_SHORT).show()
        }
}