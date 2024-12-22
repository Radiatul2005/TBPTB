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

    // State dari ViewModel
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
                .fillMaxHeight(0.80f)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
                .align(Alignment.TopCenter)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Sign Up",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(40.dp))
                // Modifier yang konsisten untuk TextField
                val textFieldModifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(vertical = 4.dp)

                // Input Name
                TextField(
                    value = name.value,
                    onValueChange = { name.value = it },
                    label = { Text("Name") },
                    modifier = textFieldModifier,
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

                Spacer(modifier = Modifier.height(20.dp))

                // Input Email with Validation
                TextField(
                    value = email.value,
                    onValueChange = {
                        email.value = it
                        emailError = if (Patterns.EMAIL_ADDRESS.matcher(it).matches()) "" else "Invalid email format"
                    },
                    label = { Text("Email") },
                    isError = emailError.isNotEmpty(),
                    supportingText = { if (emailError.isNotEmpty()) Text(emailError, color = Color.Red) },
                    modifier = textFieldModifier,
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

                Spacer(modifier = Modifier.height(20.dp))

                // Input Password with Toggle and Validation
                TextField(
                    value = password.value,
                    onValueChange = {
                        password.value = it
                        passwordError = if (it.length >= 6) "" else "Password must be at least 6 characters"
                    },
                    label = { Text("Password") },
                    isError = passwordError.isNotEmpty(),
                    supportingText = { if (passwordError.isNotEmpty()) Text(passwordError, color = Color.Red) },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = textFieldModifier,
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

                Spacer(modifier = Modifier.height(20.dp))

                // Input Confirm Password with Toggle and Validation
                TextField(
                    value = confirmPassword.value,
                    onValueChange = {
                        confirmPassword.value = it
                        confirmPasswordError = if (it == password.value) "" else "Passwords do not match"
                    },
                    label = { Text("Confirm Password") },
                    isError = confirmPasswordError.isNotEmpty(),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    supportingText = { if (confirmPasswordError.isNotEmpty()) Text(confirmPasswordError, color = Color.Red) },
                    modifier = textFieldModifier,
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

                Spacer(modifier = Modifier.height(32.dp))

                // Button "Create Account"
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
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF469C8F))
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Create Account", color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Error Message
                errorMessage?.let {
                    Text(text = it, color = Color.Red, style = MaterialTheme.typography.bodyMedium)
                }
                LaunchedEffect(registerStatus) {
                    registerStatus?.let {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        if (registerStatus == "Registration Successful")
                            navController.navigate("login") {
                                popUpTo("register") { inclusive = true }  // Pop the login screen from back stack
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
