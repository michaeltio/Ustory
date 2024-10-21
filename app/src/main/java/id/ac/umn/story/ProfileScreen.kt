package id.ac.umn.story

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import id.ac.umn.story.ui.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid
    var user by remember { mutableStateOf<User?>(null) }
    var userPosts by remember { mutableStateOf<List<Post>>(emptyList()) }
    var expanded by remember { mutableStateOf(false) }


    LaunchedEffect(userId) {
        if (userId != null) {
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    user = document.toObject(User::class.java)
                }
                .addOnFailureListener { e ->
                    Log.e("ProfileScreen", "Error fetching user data", e)
                }

            firestore.collection("posts")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { documents ->
                    userPosts = documents.map { it.toObject(Post::class.java) }
                }
                .addOnFailureListener { e ->
                    Log.e("ProfileScreen", "Error fetching user posts", e)
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                actions = {
                    Box {
                        IconButton(onClick = { expanded = true }) {
                            Icon(painterResource(id = R.drawable.menu_icon), contentDescription = "More")
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Log Out") },
                                onClick = {
                                    auth.signOut()
                                    navController.navigate("login") {
                                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            )
        },
        bottomBar = { BottomNavBar(navController) }
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                user?.let {
                    Image(
                        painter = painterResource(id = R.drawable.profile),
                        contentDescription = "User Photo",
                        modifier = Modifier
                            .size(128.dp)
                            .clickable { }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = it.username,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Edit Profile",
                        color = PrimaryBlue,
                        modifier = Modifier.clickable { navController.navigate("edit_profile") }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Biography",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = it.biography,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "NIM: ${it.nim}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Posts",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            items(userPosts) { post ->
                PostItem(post)
            }
        }
    }
}

@Preview
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(rememberNavController())
}