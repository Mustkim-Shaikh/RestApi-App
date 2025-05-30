package com.example.testproject.presentation.otp

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testproject.data.model.VerifyOtpRequest
import com.example.testproject.data.repository.AuthRepository
import com.example.testproject.utils.TokenStore
import kotlinx.coroutines.launch
import kotlin.math.log

class OtpViewModel(private val repository: AuthRepository) : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var otp by mutableStateOf("")
    var success by mutableStateOf(false)

    fun verifyOtp(
        phoneNumber: String,
        countryCode: String,
        context: Context,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            Log.d("OtpViewModel", "Verifying OTP: $otp for $countryCode$phoneNumber")

            try {
                val response = repository.verifyOtp(
                    VerifyOtpRequest(phoneNumber, countryCode, otp)
                )

                Log.d("OtpViewModel", "Response received: ${response.code()} - Success: ${response.isSuccessful}")

                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("OtpViewModel", "Response Body: $body")

                    if (response.isSuccessful) {
                        val body = response.body()
                        Log.d("OtpViewModel", "Response Body: $body")

                        val token = body?.data?.jwt
                        val refresh = body?.data?.refreshToken

                        if (body?.success == true && !token.isNullOrEmpty() && !refresh.isNullOrEmpty()) {
                            TokenStore.saveTokens(context, token, refresh)
                            success = true
                            onSuccess()
                        } else {
                            errorMessage = "Tokens missing in response: ${body?.message}"
                            Log.e("OtpViewModel", "Tokens missing: jwt=$token, refreshToken=$refresh")
                        }
                    } else {
                        errorMessage = "Verification failed: ${response.code()}"
                    }

                } else {
                    errorMessage = "Verification failed: ${response.code()}"
                    Log.d("OtpViewModel", "Error response: $errorMessage")
                }
            } catch (e: Exception) {
                errorMessage = "Network error: ${e.localizedMessage}"
                Log.e("OtpViewModel", "Exception occurred: ${e.localizedMessage}", e)
            } finally {
                isLoading = false
            }
        }
    }

}
