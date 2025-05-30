package com.example.testproject.data.model

data class ProductResponse(
    val success: Boolean,
    val message: String,
    val data: List<Product>, // This is the crucial change! Your products are in a 'data' array
    val statusCode: Int
)

data class Product(
    val _id: String,
    val productId: String,
    val title: String,
    val subTitle: String,
    val subTitle2: String,
    val price: Int,
    val mrp: Int,
    val images: List<ProductImage>,
    val stockStatus: String
)

data class ProductImage(
    val url: String,
    val primary: Boolean
)
// Extension function
fun Product.getPrimaryImage(): String? {
    return this.images.firstOrNull { it.primary }?.url ?: this.images.firstOrNull()?.url
}