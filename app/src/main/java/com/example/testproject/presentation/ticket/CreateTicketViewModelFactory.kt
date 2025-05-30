package com.example.testproject.presentation.ticket

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.testproject.data.repository.AuthRepositoryImpl

class CreateTicketViewModelFactory(
    private val application: Application,
    private val repository: AuthRepositoryImpl
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateTicketViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CreateTicketViewModel(repository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}