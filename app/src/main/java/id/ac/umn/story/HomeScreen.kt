package id.ac.umn.story

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun HomeScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val user = auth.currentUser
    val username = remember { mutableStateOf("") }
    val posts = remember { mutableStateListOf<Pair<String, Map<String, Any>>>() }

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
                    posts.add(document.id to document.data)
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
            Text("Welcome, ${username.value}", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn {
                items(posts) { (postId, post) ->
                    PostItem(postId, post, firestore)
                }
            }
        }
    }
}
@Composable
fun PostItem(postId: String, post: Map<String, Any>, firestore: FirebaseFirestore) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    var likes by remember { mutableStateOf((post["likes"] as? Long) ?: 0L) }
    var hasLiked by remember { mutableStateOf(false) }

    LaunchedEffect(postId) {
        user?.let {
            firestore.collection("posts").document(postId)
                .collection("likes").document(it.uid).get()
                .addOnSuccessListener { document ->
                    hasLiked = document.exists()
                }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color.White, RoundedCornerShape(8.dp)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = post["username"] as String, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = post["caption"] as String, style = MaterialTheme.typography.bodyMedium)
            post["imageUrl"]?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(vertical = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "$likes likes", style = MaterialTheme.typography.bodySmall)
                Icon(
                    painter = painterResource(id = if (hasLiked) R.drawable.liked else R.drawable.like),
                    contentDescription = null,
                    tint = if (hasLiked) Color.Red else Color.Gray,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            if (!hasLiked) {
                                likes++
                                user?.let {
                                    val likeData = hashMapOf("userId" to it.uid)
                                    firestore.collection("posts").document(postId)
                                        .collection("likes").document(it.uid).set(likeData)
                                        .addOnSuccessListener {
                                            firestore.collection("posts").document(postId)
                                                .update("likes", likes)
                                                .addOnSuccessListener {
                                                    hasLiked = true
                                                }
                                        }
                                }
                            }
                        }
                )
            }
        }
    }
}