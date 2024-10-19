package id.ac.umn.story

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import id.ac.umn.story.ui.theme.PrimaryBlue

@Composable
fun LandingScreen(navController: NavController) {
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
                text = "Welcome to UStory",
                style = MaterialTheme.typography.headlineLarge,
                color = PrimaryBlue,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navController.navigate("login") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(PrimaryBlue)
            ) {
                Text(text = "Login", color = Color.White)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { navController.navigate("register") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(PrimaryBlue)
            ) {
                Text(text = "Register", color = Color.White)
            }
        }
    }
}