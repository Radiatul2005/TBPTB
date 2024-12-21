package com.example.tbtb.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tbtb.ui.common.BottomNavigationBar
import com.example.tbtb.ui.common.Screen
import com.example.tbtb.ui.home.HomeScreen
import com.example.tbtb.ui.initialview.InitialViewScreen
import com.example.tbtb.ui.login.LoginScreen
import com.example.tbtb.ui.profile.EditProfileScreen
import com.example.tbtb.ui.profile.ProfileScreen
import com.example.tbtb.ui.project.AddProjectScreen
import com.example.tbtb.ui.project.ProjectDetailScreen
import com.example.tbtb.ui.project.ProjectScreen
import com.example.tbtb.ui.proposal.AddProposalScreen
import com.example.tbtb.ui.register.RegisterScreen
import com.example.tbtb.ui.splash.SplashScreen
import com.example.tbtb.ui.task.AddTaskScreen
import com.example.tbtb.ui.task.TaskDetailScreen
import com.example.tbtb.ui.theme.TBTBTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TBTBTheme {
                AppNavigation()
            }
        }
    }

}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "NewApi")
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    Scaffold(

    ) { _ ->
        NavHost(
            navController = navController,
            startDestination = "splash"
        ) {
            // Rute-rute aplikasi
            composable("splash") { SplashScreen(navController = navController) }
            composable("login") { LoginScreen(navController = navController) }
            composable("register") { RegisterScreen(navController = navController) }
            composable("initialView") { InitialViewScreen(navController = navController)   }
            composable("add") { AddProjectScreen(navController = navController) }

            composable(Screen.Home.route) { HomeScreen(navController = navController) }
            composable(Screen.Project.route) { ProjectScreen(navController = navController) }
            composable(Screen.Profile.route) { ProfileScreen(navController = navController) }

            composable("add") { AddProjectScreen(navController = navController) }
            composable("detail/{projectId}") { backStackEntry ->
                val projectId = backStackEntry.arguments?.getString("projectId")
                projectId?.let {
                    ProjectDetailScreen(navController = navController, projectId = it)
                }
            }
            composable("addTask/{projectId}") { backStackEntry ->
                val projectId = backStackEntry.arguments?.getString("projectId")
                projectId?.let {
                    AddTaskScreen(navController = navController, projectId = it)
                }
            }
            composable("addProposal/{projectId}") { backStackEntry ->
                val projectId = backStackEntry.arguments?.getString("projectId")
                projectId?.let {
                    AddProposalScreen(navController = navController, projectId = it)
                }
            }
            composable("taskDetail/{taskId}") { backStackEntry ->
                val proposalId = backStackEntry.arguments?.getString("taskId")
                proposalId?.let {
                    TaskDetailScreen(navController = navController, taskId = it)
                }
            }
            composable("editProfile") { EditProfileScreen(navController = navController) }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AppNavigationPreview() {
    TBTBTheme {
        AppNavigation()
    }
}
