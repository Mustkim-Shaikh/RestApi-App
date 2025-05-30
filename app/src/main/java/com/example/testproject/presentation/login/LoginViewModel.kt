package com.example.testproject.presentation.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testproject.data.model.SendOtpRequest
import com.example.testproject.data.remote.RetrofitClient
import com.example.testproject.data.repository.AuthRepository
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class LoginViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var successMessage by mutableStateOf<String?>(null)
        private set

    fun sendOtp(
        phoneNumber: String = "9899500873",
        countryCode: String = "91",
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            successMessage = null

            try {
                val response = repository.sendOtp(SendOtpRequest(phoneNumber, countryCode))

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true) {
                        successMessage = body.message
                        onSuccess()
                    } else {
                        errorMessage = body?.message ?: "Something went wrong!"
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    errorMessage = try {
                        val json = JSONObject(errorBody ?: "")
                        json.optString("message", "Error ${response.code()}")
                    } catch (e: Exception) {
                        "Server Error (${response.code()})"
                    }
                }

            } catch (e: IOException) {
                errorMessage = "Network error: Check your connection."
            } catch (e: HttpException) {
                errorMessage = "Unexpected server error (${e.code()})"
            } catch (e: Exception) {
                errorMessage = "Unexpected error: ${e.localizedMessage ?: "Unknown error"}"
            } finally {
                isLoading = false
            }
        }
    }
}
