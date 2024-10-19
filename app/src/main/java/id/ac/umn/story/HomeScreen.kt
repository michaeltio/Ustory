package id.ac.umn.story

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun HomeScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val user = auth.currentUser
    val username = remember { mutableStateOf("") }
    val posts = remember { mutableStateListOf<Map<String, Any>>() }

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

        firestore.collection("posts").get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    posts.add(document.data)
                }
            }
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text("Welcome, ${username.value}")
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn {
                items(posts) { post ->
                    PostItem(post, firestore)
                }
            }
        }
    }
}

@Composable
fun PostItem(post: Map<String, Any>, firestore: FirebaseFirestore) {
    var likes by remember { mutableStateOf((post["likes"] as? Long) ?: 0L) }
    val postId = post["postId"] as? String

    Column(modifier = Modifier.padding(8.dp)) {
        Text(text = post["username"] as String, style = MaterialTheme.typography.headlineSmall)
        Text(text = post["caption"] as String, style = MaterialTheme.typography.bodyMedium)
        post["imageUrl"]?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Text(text = "$likes likes", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                if (postId != null) {
                    likes++
                    firestore.collection("posts").document(postId)
                        .update("likes", likes)
                        .addOnFailureListener { e ->
                            // Handle the error
                            Log.e("PostItem", "Error updating likes", e)
                        }
                } else {
                    Log.e("PostItem", "postId is null")
                }
            }) {
                Text("Like")
            }
        }
    }
}