package com.example.testproject.presentation.product

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testproject.data.model.Product
import com.example.testproject.data.repository.AuthRepositoryImpl
import com.example.testproject.utils.TokenStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductListViewModel(
    private val repository: AuthRepositoryImpl,
    private val application: Application // Changed to Application context
) : ViewModel() {

    private val _productState = MutableStateFlow<List<Product>>(emptyList())
    val productState: StateFlow<List<Product>> = _productState

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var accessToken: String? = null // Store the token once fetched

    init {
        viewModelScope.launch {
            accessToken = TokenStore.getAccessToken(application.applicationContext)
            Log.d("ProductListViewModel","$accessToken")
                if (accessToken.isNullOrEmpty()) {
                _error.value = "Authentication token not found. Please log in."
                _isLoading.value = false
                Log.e("ProductListViewModel", "Authentication token is missing or empty.")
            } else {
                fetchProducts()
            }
        }
    }

    // Made public so it can be called for retry after an error
    fun fetchProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            // Re-check token in case it was null initially and a retry is triggered
            if (accessToken.isNullOrEmpty()) {
                accessToken = TokenStore.getAccessToken(application.applicationContext)
                if (accessToken.isNullOrEmpty()) {
                    _error.value = "Authentication token not found. Cannot fetch products."
                    _isLoading.value = false
                    Log.e("ProductListViewModel", "Authentication token is still missing or empty during retry.")
                    return@launch // Exit if token is still missing
                }
            }

            try {
                // Use the stored accessToken
                val response = repository.getProducts("$accessToken")
                Log.d("ProductListViewModel", "Attempting to fetch with token: Bearer $accessToken")

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    val products = apiResponse?.data ?: emptyList()
                    _productState.value = products
                    Log.d("ProductListViewModel", "Products fetched successfully. Count: ${products.size}")
                    products.take(3).forEach { product ->
                        Log.d("ProductListViewModel", "Product: ${product.title}, Price: ${product.price}")
                    }
                } else {
                    val errorMessage = "Failed to fetch products: ${response.code()} ${response.message()}"
                    _error.value = errorMessage
                    _productState.value = emptyList()
                    Log.e("ProductListViewModel", errorMessage)
                }
            } catch (e: Exception) {
                _error.value = "Network error: ${e.localizedMessage ?: "Unknown error"}"
                _productState.value = emptyList()
                Log.e("ProductListViewModel", "Exception while fetching products: ${e.localizedMessage}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}