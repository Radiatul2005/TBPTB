package com.example.tbtb.ui.register

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController
) {
    val registerViewModel: RegisterViewModel = viewModel()
    val name = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }
    val context = LocalContext.current
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }
    val passwordVisible by remember { mutableStateOf(false) }

    val registerStatus by registerViewModel.registerStatus.collectAsState()
    val isLoading by registerViewModel.isLoading.collectAsState()
    val errorMessage by registerViewModel.errorMessage.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF469C8F))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
                )
                .align(Alignment.TopCenter)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .padding(top = 40.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Sign Up",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Name Field
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Name",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                    TextField(
                        value = name.value,
                        onValueChange = { name.value = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.LightGray,
                            focusedIndicatorColor = Color(0xFF469C8F)
                        ),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Email Field
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Email",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                    TextField(
                        value = email.value,
                        onValueChange = {
                            email.value = it
                            emailError = if (Patterns.EMAIL_ADDRESS.matcher(it).matches()) "" else "Invalid email format"
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.LightGray,
                            focusedIndicatorColor = Color(0xFF469C8F)
                        ),
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
                        onValueChange = {
                            password.value = it
                            passwordError = if (it.length >= 6) "" else "Password must be at least 6 characters"
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.LightGray,
                            focusedIndicatorColor = Color(0xFF469C8F)
                        ),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Confirm Password Field
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Confirm Password",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                    TextField(
                        value = confirmPassword.value,
                        onValueChange = {
                            confirmPassword.value = it
                            confirmPasswordError = if (it == password.value) "" else "Passwords do not match"
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.LightGray,
                            focusedIndicatorColor = Color(0xFF469C8F)
                        ),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Create Account Button
                Button(
                    onClick = {
                        emailError = if (Patterns.EMAIL_ADDRESS.matcher(email.value).matches()) "" else "Invalid email format"
                        passwordError = if (password.value.length >= 6) "" else "Password must be at least 6 characters"
                        confirmPasswordError = if (confirmPassword.value == password.value) "" else "Passwords do not match"

                        if (emailError.isEmpty() && passwordError.isEmpty() && confirmPasswordError.isEmpty()) {
                            registerViewModel.register(name.value, email.value, password.value)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF469C8F)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Create Account", color = Color.White)
                    }
                }

                // Error handling remains the same
                LaunchedEffect(registerStatus) {
                    registerStatus?.let {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        if (registerStatus == "Registration Successful")
                            navController.navigate("login") {
                                popUpTo("register") { inclusive = true }
                            }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    val navController = rememberNavController()
    RegisterScreen(navController)
}
