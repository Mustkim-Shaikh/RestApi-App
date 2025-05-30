package com.example.testproject.presentation.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.testproject.data.remote.AuthApi
import com.example.testproject.data.remote.RetrofitClient
import com.example.testproject.data.repository.AuthRepositoryImpl

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current

    // Set up repository and ViewModel manually
    val viewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(
            AuthRepositoryImpl(RetrofitClient.api)
        )
    )

    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage
    val successMessage = viewModel.successMessage

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Login", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = "91",
            onValueChange = {},
            label = { Text("Country Code") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = "9899500873",
            onValueChange = {},
            label = { Text("Phone Number") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                viewModel.sendOtp(
                    onSuccess = {
                        navController.navigate("otp_screen/9899500873/91")

                    })
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = Color.White
                )
            } else {
                Text("Send OTP")
            }
        }

        // Success Message
        successMessage?.let {
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = it, color = Color(0xFF2E7D32)) // Green
        }

        // Error Message
        errorMessage?.let {
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = it, color = Color.Red)
        }
    }
}
