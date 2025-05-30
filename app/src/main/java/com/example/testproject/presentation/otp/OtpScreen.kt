package com.example.testproject.presentation.otp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.testproject.data.remote.RetrofitClient
import com.example.testproject.data.repository.AuthRepositoryImpl


@Composable
fun OtpScreen(
    navController: NavController,
    phoneNumber: String,
    countryCode: String
) {
    val context = LocalContext.current
    val viewModel: OtpViewModel = viewModel(
        factory = OtpViewModelFactory(AuthRepositoryImpl(RetrofitClient.api))
    )

    val isLoading = viewModel.isLoading
    val error = viewModel.errorMessage
    val otp = viewModel.otp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Enter OTP", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = otp,
            onValueChange = { viewModel.otp = it },
            label = { Text("OTP") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                println("OTP Submitted: ${viewModel.otp}")
                viewModel.verifyOtp(
                    phoneNumber = phoneNumber,
                    countryCode = countryCode,
                    context = context
                ) {
                    println("Navigation to shopScreen triggered")
                    navController.navigate("shopScreen")
                }
            },

            ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Verify OTP")
            }
        }

        error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = Color.Red)
        }
    }
}
