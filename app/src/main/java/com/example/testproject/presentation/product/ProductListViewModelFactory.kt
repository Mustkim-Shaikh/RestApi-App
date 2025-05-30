// ProductListViewModelFactory.kt
import android.app.Application // Use Application context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.testproject.data.repository.AuthRepositoryImpl
import com.example.testproject.presentation.product.ProductListViewModel

class ProductListViewModelFactory(
    private val application: Application, // Changed to Application
    private val repository: AuthRepositoryImpl
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            // Pass the application context to the ViewModel
            return ProductListViewModel(repository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}