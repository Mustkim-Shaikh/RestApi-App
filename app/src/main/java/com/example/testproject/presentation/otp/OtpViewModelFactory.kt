package com.example.testproject.presentation.otp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.testproject.data.repository.AuthRepository

class OtpViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return OtpViewModel(repository) as T
    }
}
