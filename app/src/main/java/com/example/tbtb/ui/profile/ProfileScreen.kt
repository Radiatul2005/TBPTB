package com.example.tbtb.ui.profile

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.example.tbtb.R
import com.example.tbtb.data.model.UserData
import com.example.tbtb.ui.common.BottomNavigationBar
import com.example.tbtb.ui.common.Screen

@Composable
fun ProfileScreen(navController: NavController) {
    val viewModel: ProfileViewModel = viewModel()
    val currentUser by viewModel.currentUser.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    val token = sharedPreferences.getString("user_token", "") ?: ""
    // State for showing the logout confirmation dialog
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Fetch current user only once when the screen loads
    LaunchedEffect(Unit) {
        viewModel.getCurrentUser(token)
    }

    when {
        isLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF7F8FA)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return
        }
        (!errorMessage.isNullOrEmpty())->{
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            return
        }
        else ->
            Scaffold(
                modifier = Modifier.statusBarsPadding(),
                bottomBar = {
                    BottomNavigationBar(navController)

                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF7F8FA))
                        .padding(innerPadding)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFF7F8FA))
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        ) {
                            // Title
                            Text(
                                text = "My Profile",
                                style = MaterialTheme.typography.headlineLarge,
                                modifier = Modifier.padding(bottom = 16.dp),
                                color = MaterialTheme.colorScheme.primary
                            )

                            currentUser?.data?.let { userData ->
                                // Profile card
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = MaterialTheme.shapes.medium,
                                    elevation = CardDefaults.cardElevation(4.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(24.dp)
                                            .fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        val profileImageUrl = userData.photo_url

                                        if (profileImageUrl.isNullOrEmpty()) {
                                            Image(
                                                painter = painterResource(id = R.drawable.ic_profile),
                                                contentDescription = "Profile Image",
                                                modifier = Modifier
                                                    .size(120.dp)
                                                    .clip(CircleShape)
                                                    .background(Color.Gray),
                                                contentScale = ContentScale.Crop
                                            )
                                        } else {
                                            AsyncImage(
                                                model = profileImageUrl,
                                                contentDescription = "Profile Image",
                                                modifier = Modifier
                                                    .size(120.dp)
                                                    .clip(CircleShape)
                                                    .background(Color.White),
                                                placeholder = painterResource(id = R.drawable.ic_profile),
                                                error = painterResource(id = R.drawable.ic_broken),
                                                contentScale = ContentScale.Crop,
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))

                                        // User Name
                                        Text(
                                            text = userData.nama ?: "N/A",
                                            style = MaterialTheme.typography.headlineLarge,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )

                                        // User Email
                                        Text(
                                            text = userData.email ?: "N/A",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                // Edit Profile Button
                                Button(
                                    onClick = {
                                        navController.navigate("editProfile")
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    shape = MaterialTheme.shapes.medium
                                ) {
                                    Text("Edit Profile", style = MaterialTheme.typography.bodyLarge)
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Logout Button
                                Button(
                                    onClick = {
                                        showLogoutDialog = true
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    shape = MaterialTheme.shapes.medium,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                                ) {
                                    Text("Logout", style = MaterialTheme.typography.bodyLarge, color = Color.White)
                                }
                                if (showLogoutDialog) {
                                    AlertDialog(
                                        onDismissRequest = { showLogoutDialog = false },
                                        title = { Text("Confirm Logout") },
                                        text = { Text("Are you sure you want to logout?") },
                                        confirmButton = {
                                            TextButton(
                                                onClick = {
                                                    sharedPreferences.edit().clear().apply()
                                                    navController.navigate("login") {
                                                        popUpTo(Screen.Profile.route) { inclusive = true }
                                                        popUpTo(Screen.Home.route) { inclusive = true }
                                                        launchSingleTop = true
                                                        restoreState = false

                                                    }
                                                    showLogoutDialog = false
                                                }
                                            ) {
                                                Text("Yes")
                                            }
                                        },
                                        dismissButton = {
                                            TextButton(onClick = { showLogoutDialog = false }) {
                                                Text("No")
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }


            }
    }


}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(navController = rememberNavController())
}
