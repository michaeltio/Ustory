package id.ac.umn.story

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import id.ac.umn.story.ui.theme.UStoryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UStoryTheme {
                val navController = rememberNavController()
                val isLoggedIn = getLoginState(this)
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    NavigationComponent(navController, isLoggedIn)
                }
            }
        }
    }

    //ngambil data login jika tersimpan di shared preferences
    private fun getLoginState(context: Context): Boolean {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("is_logged_in", false)
    }
}

//daftar route
@Composable
fun NavigationComponent(navController: NavHostController, isLoggedIn: Boolean) {
    NavHost(navController = navController, startDestination = if (isLoggedIn) "home" else "landing") {
        composable("landing") { LandingScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable("post") { PostScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
        composable("edit_profile") { EditProfileScreen(navController) }
    }
}