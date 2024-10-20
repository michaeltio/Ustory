package id.ac.umn.story

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import id.ac.umn.story.R

@Composable
fun PostItem(post: Post) {
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    var likes by remember { mutableStateOf(post.likes) }
    var likedByUser by remember { mutableStateOf(currentUser?.uid in post.likedBy) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (post.imageUrl.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(post.imageUrl),
                    contentDescription = "Post Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Text(text = post.caption, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            Text(text = post.username, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Likes: $likes", style = MaterialTheme.typography.bodyMedium)
                Icon(
                    painter = painterResource(id = if (likedByUser) R.drawable.liked else R.drawable.like),
                    contentDescription = "Like Button",
                    tint = if(likedByUser) Color.Red else Color.Gray,
                    modifier = Modifier.clickable {
                        currentUser?.let { user ->
                            val userId = user.uid
                            if (likedByUser) {
                                likes -= 1
                                likedByUser = false
                                firestore.collection("posts").document(post.postId)
                                    .update("likes", likes, "likedBy", FieldValue.arrayRemove(userId))
                            } else {
                                likes += 1
                                likedByUser = true
                                firestore.collection("posts").document(post.postId)
                                    .update("likes", likes, "likedBy", FieldValue.arrayUnion(userId))
                            }
                        }
                    }
                )
            }
        }
    }
}

data class Post(
    val caption: String = "",
    val likes: Int = 0,
    val postId: String = "",
    val userId: String = "",
    val username: String = "",
    val imageUrl: String = "",
    val timestamp: Long = 0L,
    val likedBy: List<String> = emptyList()
)