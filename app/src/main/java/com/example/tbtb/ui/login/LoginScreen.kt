package com.example.tbtb.ui.login

import android.content.Context
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tbtb.R
import com.example.tbtb.ui.common.LoginState
import com.example.tbtb.ui.common.Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, viewModel: LoginViewModel = viewModel()) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    val loginStatus by viewModel.loginStatus.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF469C8F))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Image(
                painter = painterResource(id = R.drawable.logo_ur_app),
                contentDescription = "Logo Unand Research",
                modifier = Modifier.size(150.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "Login",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.Black,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    // Email Field
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Email",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                        TextField(
                            value = email.value,
                            onValueChange = { email.value = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.LightGray,
                                focusedIndicatorColor = Color(0xFF469C8F),
                                cursorColor = Color(0xFF469C8F),
                                unfocusedTextColor = Color.Black,
                                focusedTextColor = Color.Black
                            ),
                            textStyle = LocalTextStyle.current.copy(color = Color.Black),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Password Field
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Password",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                        TextField(
                            value = password.value,
                            onValueChange = { password.value = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.LightGray,
                                focusedIndicatorColor = Color(0xFF469C8F),
                                cursorColor = Color(0xFF469C8F),
                                unfocusedTextColor = Color.Black,
                                focusedTextColor = Color.Black
                            ),
                            textStyle = LocalTextStyle.current.copy(color = Color.Black),
                            singleLine = true,
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        painter = painterResource(
                                            id = if (passwordVisible) R.drawable.ic_visibility else R.drawable.ic_visibility_off
                                        ),
                                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                                    )
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    Button(
                        onClick = {
                            emailError = ""
                            passwordError = ""
                            if (!Patterns.EMAIL_ADDRESS.matcher(email.value).matches()) {
                                emailError = "Invalid email format"
                            } else if (password.value.length < 6) {
                                passwordError = "Password must be at least 6 characters"
                            } else {
                                viewModel.login(email.value, password.value)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF469C8F)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White
                            )
                        } else {
                            Text(
                                "Login",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }

                    errorMessage?.let {
                        Text(
                            text = it,
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    LaunchedEffect(loginStatus) {
                        if (loginStatus is LoginState.Success) {
                            Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                            navController.navigate(Screen.Home.route) {
                                popUpTo("login") { inclusive = true }
                                popUpTo("initialView") { inclusive = true }
                            }
                        } else if (loginStatus is LoginState.Error) {
                            Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show()
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Don't have an account?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Sign up",
                            modifier = Modifier.clickable { navController.navigate("register") },
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF469C8F)
                        )
                    }
                }
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    val navController = rememberNavController()
    LoginScreen(navController = navController)
}
