package com.example.tbtb.ui.splash

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.tbtb.R
import com.example.tbtb.ui.common.Screen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val token = sharedPreferences.getString("user_token", null)

    LaunchedEffect(key1 = Unit) {
        delay(2000) // Durasi delay splash screen
        if (token.isNullOrEmpty()) {
            // Arahkan ke login jika tidak ada token
            navController.navigate("initialView") {
                popUpTo("splash") { inclusive = true }
            }
        } else {
            // Arahkan ke main jika token ada
            navController.navigate(Screen.Home.route) {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    // UI Splash Screen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF469C8F)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_ur_app),
            contentDescription = "Logo Aplikasi"
        )
    }
}
@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen(navController = rememberNavController())
}